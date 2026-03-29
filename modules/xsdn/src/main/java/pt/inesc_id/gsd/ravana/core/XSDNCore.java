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
import pt.inesc_id.gsd.ravana.flow.XSDNFlow;
import pt.inesc_id.gsd.ravana.intents.XSDNPolicy;
import pt.inesc_id.gsd.ravana.network.XSDNNode;

import java.util.*;

/**
 * The core XSDN Singleton class
 */
public class XSDNCore {
    private static Logger logger = LogManager.getLogger(XSDNCore.class.getName());

    protected static Map<String, XSDNNode> xSDNNodes = new HashMap<>();
    protected static Map<String, XSDNFlow> xSDNFlows = new HashMap<>();
    protected static Map<String, XSDNPolicy> xSDNPolicies = new HashMap<>();

    private static XSDNCore instance = null;

    protected XSDNCore() {
        // Exists only to defeat instantiation.
    }

    public static XSDNCore getInstance() {
        if (instance == null) {
            instance = new XSDNCore();
        }
        return instance;
    }

    /**
     * Adds a new XSDN node to the list of XSDN nodes
     *
     * @param xsdnNode a node
     */
    public static void addXSDNNode(String id, XSDNNode xsdnNode) {
        xSDNNodes.put(id, xsdnNode);
    }

    /**
     * Adds a new XSDN flow to the list of XSDN flows
     *
     * @param xsdnFlow a flow
     */
    public static void addXSDNFlow(String id, XSDNFlow xsdnFlow) {
        xSDNFlows.put(id, xsdnFlow);
    }

    /**
     * Adds a new XSDN policy to the list of XSDN Policies
     *
     * @param xsdnPolicy a policy
     */
    public static void addXSDNPolicy(String id, XSDNPolicy xsdnPolicy) {
        xSDNPolicies.put(id, xsdnPolicy);
    }

    public static Map<String, XSDNNode> getXSDNNodes() {
        return xSDNNodes;
    }

    public static Map<String, XSDNFlow> getXSDNFlows() {
        return xSDNFlows;
    }

    public static Map<String, XSDNPolicy> getxSDNPolicies() {
        return xSDNPolicies;
    }

    /**
     * Gets the ids of the next nodes of any given node id.
     * @param nodeId, current node id
     * @return set of next node ids.
     */
    public static Set<String> getNextNodes(String nodeId) {
        XSDNNode xsdnNode = xSDNNodes.get(nodeId);
        Set<String> nodes = xsdnNode.returnAllNextNodes();

        if (logger.isDebugEnabled()) {
            for (String setElement : nodes) {
                logger.debug(setElement);
            }
        }
        return nodes;
    }

    /**
     * Gets the origin of any flow
     * @param flowId the flow Id
     * @return the origin
     */
    public static String getOrigin(String flowId) {
        XSDNFlow xsdnFlow = xSDNFlows.get(flowId);
        return xsdnFlow.getOrigin();
    }

    /**
     * Gets the destination of any flow
     * @param flowId the flow Id
     * @return the destination
     */
    public static String getDestination(String flowId) {
        XSDNFlow xsdnFlow = xSDNFlows.get(flowId);
        return xsdnFlow.getDestination();
    }

    /**
     * Gets the property value of a link
     * @param currentNodeId, Id of the current node
     * @param neighbourNodeId, Id of the neighbour node
     * @param propertyName, name of the property that is considered
     * @return the property value.
     */
    public static double getProperty(String currentNodeId, String neighbourNodeId, String propertyName) {
        XSDNNode xsdnNode = xSDNNodes.get(currentNodeId);
        return xsdnNode.getProperty(neighbourNodeId, propertyName);
    }

    /**
     * Gets the property value of a link
     * @param currentNodeId, Id of the current node
     * @param neighbourNodeId, Id of the neighbour node
     * @return the speed value.
     */
    public static double getSpeedOfLink(String currentNodeId, String neighbourNodeId) {
        logger.info("current node id: " + currentNodeId + " .neighbour node id: " + neighbourNodeId + " .Speed: " + getProperty(currentNodeId,neighbourNodeId,"speed"));
        return getProperty(currentNodeId,neighbourNodeId,"speed");
    }
}
