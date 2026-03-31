/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.infinispan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.infinispan.Cache;
import pt.inesc_id.gsd.ravana.algorithms.CyclesTruncatedRandomWalk;
import pt.inesc_id.gsd.ravana.builders.FlowBuilder;
import pt.inesc_id.gsd.ravana.builders.Parser;
import pt.inesc_id.gsd.ravana.constants.XSDNConstants;
import pt.inesc_id.gsd.ravana.core.XSDNCore;

import java.io.File;
import java.util.*;

/**
 * RouteInitiator class with Infinispan integration
 */
public class InfRouteInitiator extends XSDNCore {
    private static Logger logger = LogManager.getLogger(InfRouteInitiator.class.getName());
    protected static final Set<String> flows = getXSDNFlows().keySet();
    protected static Cache defaultCache;


    /**
     * Random Walk the flow from the origin to destination
     *
     * @param flowId, the flow id.
     */
    public static void initialRoute(String flowId) {
        String origin = XSDNCore.getOrigin(flowId);
        String destination = XSDNCore.getDestination(flowId);

        String intermediaries = "";
        List<String> nodesTraversed = CyclesTruncatedRandomWalk.route(origin, destination);

        CyclesTruncatedRandomWalk.printRoute("[RouteInitiator]", origin, destination, nodesTraversed);

        String key = origin + " " + destination;

        for (int i = 1; i < nodesTraversed.size() - 1; i++) {
            intermediaries += nodesTraversed.get(i) + " ";
        }
        intermediaries = intermediaries.trim();
        List<String> routeList = new ArrayList<>();

        if (defaultCache.containsKey(key)) {
            routeList = (List<String>) defaultCache.get(key);
        }
        if (routeList.contains(intermediaries)) {
            if (logger.isTraceEnabled()) {
                String msg = intermediaries + " already exists as a route for " + key;
                logger.trace(msg);
            }
        } else {
            routeList.add(intermediaries);
            defaultCache.put(key, routeList);
            if (logger.isTraceEnabled()) {
                String msg = "Adding " + intermediaries + " as a route for " + key;
                logger.trace(msg);
            }
        }
    }


    /**
     * Initialize the initialRoute list by routing sample flows multiple times using the algorithm.
     *
     * @param times number of flows to be routed to populate the lists.
     */
    public static void initializePossibleRoutesList(int times) {
        for (String flow : flows) {
            for (int i = 0; i < times; i++) {
                initialRoute(flow);
            }
        }
        printRouteList();
        clearInitFlows();
    }

    /**
     * Prints the route list.
     */
    public static void printRouteList() {
        for (String flow : flows) {
            String origin = getOrigin(flow);
            String destination = getDestination(flow);

            List<String> routeList = getPossibleRoutesList(flow);
            if (logger.isDebugEnabled()) {
                for (String route : routeList) {
                    String msg = "Route for flow " + flow + " (" + origin + "=>" + destination + ")" + ": " + route;
                    logger.debug(msg);
                }
            }
        }
    }

    /**
     * Gets the list of possible routes
     *
     * @param origin,      the origin node
     * @param destination, the destination node
     * @return the routes list
     */
    public static List<String> getPossibleRoutesList(String origin, String destination) {
        String key = origin + " " + destination;
        if (defaultCache.containsKey(key)) {
            return (List<String>) defaultCache.get(key);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Gets the list of possible routes
     *
     * @param flowId, the flow id.
     * @return the routes list
     */
    public static List<String> getPossibleRoutesList(String flowId) {
        String origin = XSDNCore.getOrigin(flowId);
        String destination = XSDNCore.getDestination(flowId);
        return getPossibleRoutesList(origin, destination);
    }

    /**
     * Initiate the flows list
     */
    public static void init() {
        Parser flowBuilder = new FlowBuilder();
        flowBuilder.parse(XSDNConstants.CONF_FOLDER + File.separator + XSDNConstants.INIT_FLOWS_XML);
        initInfinispan();
    }

    /**
     * Initiates Infnispan.
     */
    public static void initInfinispan() {
        InfCore infiniCore = InfCore.getInfiniCore();
        defaultCache = infiniCore.getDefaultCache();
        Map<String, List<String>> possibleRoutes = new HashMap<>();
        defaultCache.putAll(possibleRoutes);
        pt.inesc_id.gsd.ravana.statistics.KnowledgeBase.initInfinispan();
    }

    private static void clearInitFlows() {
        Object[] flowArray = flows.toArray();
        for (Object aFlowArray : flowArray) {
            flows.remove(aFlowArray);
        }
    }

}
