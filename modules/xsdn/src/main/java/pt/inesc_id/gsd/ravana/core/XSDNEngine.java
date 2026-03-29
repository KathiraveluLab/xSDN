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

import java.io.File;

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
}
