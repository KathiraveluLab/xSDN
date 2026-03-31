/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.inesc_id.gsd.ravana.algorithms.ConservativeRoute;
import pt.inesc_id.gsd.ravana.algorithms.RandomRoute;
import pt.inesc_id.gsd.ravana.algorithms.RouteInitiator;
import pt.inesc_id.gsd.ravana.algorithms.RouteInitiatorWithProperty;
import pt.inesc_id.gsd.ravana.core.XSDNCore;
import pt.inesc_id.gsd.ravana.core.XSDNEngine;
import pt.inesc_id.gsd.ravana.health.HealthMonitor;
import pt.inesc_id.gsd.ravana.infinispan.InfRandomRoute;
import pt.inesc_id.gsd.ravana.infinispan.InfRouteInitiator;

import java.util.Set;

/**
 * Initiate the simulations
 */
public class Initiator {
    private static Logger logger = LogManager.getLogger(Initiator.class.getName());
    private static boolean isHealthMonitoringEnabled = false;
    private static int waitTimeInMillis;

    public static void setIsHealthMonitoringEnabled(boolean isHealthMonitoringEnabled) {
        Initiator.isHealthMonitoringEnabled = isHealthMonitoringEnabled;
    }

    public static void setWaitTimeInMillis(int waitTimeInMillis) {
        Initiator.waitTimeInMillis = waitTimeInMillis;
    }

    /**
     * Initiate the simulations (trace)
     * @param times how many times the initiator should run
     */
    public static void initiateTrace(int times) {
        initiateRouteList(times);

        XSDNEngine.initFlows();
        Set<String> flows = XSDNCore.getXSDNFlows().keySet();
        for (String flow : flows) {
            RandomRoute.traceRoute(flow);
        }

        XSDNEngine.printFlowNetworkMapSize();
        returnHealthMonitoringOutput();
    }

    /**
     * Initiate the simulations and route
     * @param initiatingTimes how many times the initiator should run
     */
    public static void simulate(int initiatingTimes) {
        initiateRouteList(initiatingTimes);

        XSDNEngine.initFlows();
        Set<String> flows = XSDNCore.getXSDNFlows().keySet();
        for (String flow : flows) {
            RandomRoute.route(flow);
        }

        XSDNEngine.printFlowNetworkMapSize();
        returnHealthMonitoringOutput();
    }

    /**
     * Just initiate the potential route list.
     * @param times, how many times the initiator should run
     */
    public static void initiateRouteList(int times) {
        initiateNetworkAndPolicies();
        RouteInitiator.init();
        RouteInitiator.printRouteList();
        String msg = XSDNEngine.returnFlowNetworkMapSize();

        if (logger.isDebugEnabled()) {
            logger.debug("Initialized the network");
        }

        RouteInitiator.initializePossibleRoutesList(times);
        logger.debug("Initial: " + msg);
    }

    /**
     * Initiates Network and Policies.
     */
    public static void initiateNetworkAndPolicies() {
        startSimulatorHealthMonitoringThread();

        XSDNEngine.initNetworkAndPolicies();
//        XSDNEngine.printFlowNetworkMapSize();

//        returnHealthMonitoringOutput();

    }

    private static void returnHealthMonitoringOutput() {
        if (isHealthMonitoringEnabled) {
            HealthMonitor.printHealthLogs();
            System.out.println("--- BENCHMARK TELEMETRY ---");
            System.out.println(HealthMonitor.getHealthLogs());
            System.out.println("---------------------------");
        }
    }

    private static void startSimulatorHealthMonitoringThread() {
        if (isHealthMonitoringEnabled) {
            HealthMonitor.setWaitTimeInMillis(waitTimeInMillis);
            Thread t = new Thread(new HealthMonitor());
            t.setDaemon(true);
            t.start();
        }
    }

    /**
     * Initiate the simulations, with Infinispan
     * @param times how many times the initiator should run
     */
    public static void infInitAndExecute(int times) {
        initiateNetworkAndPolicies();
        InfRouteInitiator.init();
        InfRouteInitiator.printRouteList();
        XSDNEngine.printFlowNetworkMapSize();

        if (logger.isDebugEnabled()) {
            logger.debug("Initialized the network");
        }

        InfRouteInitiator.initializePossibleRoutesList(times);

        XSDNEngine.initFlows();
        Set<String> flows = XSDNCore.getXSDNFlows().keySet();
        for (String flow : flows) {
            InfRandomRoute.route(flow);
        }

        XSDNEngine.printFlowNetworkMapSize();
        returnHealthMonitoringOutput();
    }

    /**
     * Execute the simulations, with Infinispan
     */
    public static void infExecute() {
        initiateNetworkAndPolicies();
        InfRouteInitiator.initInfinispan();
        InfRouteInitiator.printRouteList();
        XSDNEngine.printFlowNetworkMapSize();

        if (logger.isDebugEnabled()) {
            logger.debug("Initialized the network");
        }

        XSDNEngine.initFlows();
        Set<String> flows = XSDNCore.getXSDNFlows().keySet();
        for (String flow : flows) {
            InfRandomRoute.route(flow);
        }

        XSDNEngine.printFlowNetworkMapSize();
        returnHealthMonitoringOutput();
    }

    /**
     * Initiating the simulations
     * @param times How many times the Initiations should execute
     * @param propertyName name of the property
     * @param isDecrementIntent should the property be decremented.
     */
    public static void initiateWithProperty(int times, String propertyName, boolean isDecrementIntent) {
        initiateNetworkAndPolicies();
        RouteInitiatorWithProperty.init();
        RouteInitiatorWithProperty.printRouteList();
        XSDNEngine.printFlowNetworkMapSize();

        if (logger.isDebugEnabled()) {
            logger.debug("Initialized the network");
        }

        RouteInitiatorWithProperty.initializePossibleRoutesList(times, propertyName, isDecrementIntent);

        XSDNEngine.initFlows();
        Set<String> flows = XSDNCore.getXSDNFlows().keySet();
        for (String flow : flows) {
            ConservativeRoute.route(flow, isDecrementIntent);
        }

        XSDNEngine.printFlowNetworkMapSize();
        returnHealthMonitoringOutput();
    }
}
