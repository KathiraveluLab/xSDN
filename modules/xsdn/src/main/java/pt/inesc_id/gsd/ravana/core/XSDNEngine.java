/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.inesc_id.gsd.ravana.builders.FlowBuilder;
import pt.inesc_id.gsd.ravana.builders.NetworkBuilder;
import pt.inesc_id.gsd.ravana.builders.Parser;
import pt.inesc_id.gsd.ravana.builders.PolicyBuilder;

import pt.inesc_id.gsd.ravana.api.RoutingAlgorithmRegistry;

/**
 * Starts xSDN.
 */
public class XSDNEngine {
    private static Logger logger = LogManager.getLogger(XSDNEngine.class.getName());

    /**
     * Builds the networks, flows, and policies.
     */
    public static void initializeFlowNetwork() {
        initNetworkAndPolicies();
        initFlows();
    }

    /**
     * Initializes the flows
     */
    public static void initFlows() {
        Parser flowBuilder = new FlowBuilder();
        flowBuilder.parse();
    }

    /**
     * Initializes the network and policies.
     */
    public static void initNetworkAndPolicies() {
        Parser builder = new NetworkBuilder();
        builder.parse();
        Parser policyBuilder = new PolicyBuilder();
        policyBuilder.parse();
    }

    /**
     * Prints the status of the initialized flow network.
     */
    public static void printFlowNetworkMapSize() {
        if (logger.isDebugEnabled()) {
            logger.debug("Entries in the nodes map: " + XSDNCore.getXSDNNodes().size());
            logger.debug("Entries in the flows map: " + XSDNCore.getXSDNFlows().size());
            logger.debug("Entries in the policies map: " + XSDNCore.getxSDNPolicies().size());
        }
    }

    /**
     * Returns the status of the initialized flow network.
     */
    public static String returnFlowNetworkMapSize() {
        String msg = "";
        msg += "Entries in the nodes map: " + XSDNCore.getXSDNNodes().size() +"\n";
        msg += "Entries in the flows map: " + XSDNCore.getXSDNFlows().size() + "\n";
        msg += "Entries in the policies map: " + XSDNCore.getxSDNPolicies().size() + "\n";
        return msg;
    }

    /**
     * Executes the simulation routing pipeline (Algorithm 1) for all defined network flows.
     * @param routingAlgorithm the routing logic to apply (e.g., AdaptiveRoute, RandomRoute)
     */
    public static void executeSimulations(String routingAlgorithm) {
        boolean simulationIsExecuting = true;

        while (simulationIsExecuting) {
            boolean anyIncomplete = false;

            for (String flowId : XSDNCore.getXSDNFlows().keySet()) {
                pt.inesc_id.gsd.ravana.flow.XSDNFlow flow = XSDNCore.getXSDNFlows().get(flowId);

                if (!flow.isCompleted()) {
                    anyIncomplete = true;
                    // Per-flow algorithm override (set via algorithm="..." in flows.xml) takes precedence
                    String effectiveAlgo = (flow.getAlgorithm() != null) ? flow.getAlgorithm() : routingAlgorithm;
                    // 1. Check northbound registry for researcher-registered custom algorithms
                    if (RoutingAlgorithmRegistry.contains(effectiveAlgo)) {
                        RoutingAlgorithmRegistry.get(effectiveAlgo).route(flowId);
                    // 2. Fall back to built-in algorithms
                    } else if (effectiveAlgo.equalsIgnoreCase("RandomRoute")) {
                        pt.inesc_id.gsd.ravana.algorithms.RandomRoute.route(flowId);
                    } else if (effectiveAlgo.equalsIgnoreCase("AdaptiveRoute")) {
                        pt.inesc_id.gsd.ravana.algorithms.AdaptiveRoute.route(flowId);
                    } else {
                        logger.warn("Routing algorithm '" + effectiveAlgo
                                + "' not found in registry or built-ins. Register it via RoutingAlgorithmRegistry.register().");
                    }
                }
            }
            if (!anyIncomplete) {
                simulationIsExecuting = false;
            }
        }
        presentSummarizedOutcomes();
    }

    /**
     * Reports conclusive outcome metrics after simulation pipeline termination.
     */
    public static void presentSummarizedOutcomes() {
        logger.info("Simulation Execution Completed.");
        logger.info("Total KnowledgeBase statistics gathered natively: " + XSDNCore.getKnowledgeBase().size());
    }
}
