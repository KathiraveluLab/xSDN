/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.algorithms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.inesc_id.gsd.ravana.core.XSDNCore;
import pt.inesc_id.gsd.ravana.network.Link;
import pt.inesc_id.gsd.ravana.network.XSDNNode;
import pt.inesc_id.gsd.ravana.util.RandomUtil;

import java.util.*;

/**
 * A Random Walk implementation for the flows.
 */
public class RandomWalk extends XSDNCore implements Route {
    private static Logger logger = LogManager.getLogger(RandomWalk.class.getName());

    /**
     * Gets the ID of a next node for any given node, randomly.
     *
     * @param nodeId, the id of the current node.
     * @return the next node.
     */
    public static String getNextNode(String nodeId) {
        XSDNNode xsdnNode = xSDNNodes.get(nodeId);
        Set<String> nodes = xsdnNode.returnAllNextNodes();

        int id = RandomUtil.randomLessThanMax(nodes.size() - 1);
        int i = 0;

        String element = "0";
        for (String setElement : nodes) {
            element = setElement;
            if (i == id) {
                return setElement;
            }
            i++;
        }
        return element;
    }

    /**
     * Gets the ID of a next node for any given node, randomly, with the properties.
     *
     * @param nodeId, the id of the current node.
     * @return the next node with the properties map.
     */
    public static Link getNextNodeWithProperties(String nodeId) {
        XSDNNode xsdnNode = xSDNNodes.get(nodeId);
        Set<String> nodes = xsdnNode.returnAllNextNodes();

        int id = RandomUtil.randomLessThanMax(nodes.size() - 1);
        int i = 0;

        String element = "0";
        for (String setElement : nodes) {
            element = setElement;
            if (i == id) {
                Map<String, Double> tempProperties = xsdnNode.getAllProperties(element);
                return new Link (nodeId, element, tempProperties);
            }
            i++;
        }
        return null;
    }

    /**
     * Gets the ID of a next node for any given node, randomly, with the value of the given property.
     *
     * @param nodeId,       the id of the current node.
     * @param propertyName, name of the property.
     * @return the next node with the value of the specified property.
     */
    public static Link getNextNodeWithProperty(String nodeId, String propertyName) {
        XSDNNode xsdnNode = xSDNNodes.get(nodeId);
        Set<String> nodes = xsdnNode.returnAllNextNodes();

        int id = RandomUtil.randomLessThanMax(nodes.size() - 1);
        int i = 0;

        String element = "0";
        for (String setElement : nodes) {
            element = setElement;
            if (i == id) {
                double propertyValue = xsdnNode.getProperty(element, propertyName);
                return new Link(nodeId, element, propertyValue);
            }
            i++;
        }
        return null;
    }

    /**
     * Random Walk the flow from the origin to destination
     *
     * @param flowId, the flow id.
     */
    public static void route(String flowId) {
        String origin = XSDNCore.getOrigin(flowId);
        String destination = XSDNCore.getDestination(flowId);
        String current = origin;
        List<String> nodesTraversed = new ArrayList<>();
        nodesTraversed.add(current);

        while (!current.equalsIgnoreCase(destination)) {
            current = getNextNode(current);
            nodesTraversed.add(current);
        }

        printRoute("[Random Walk]", origin, destination, nodesTraversed);
    }

    /**
     * Prints the routing
     *
     * @param msg            some initial message
     * @param origin         origin node
     * @param destination    destination node
     * @param nodesTraversed the traversed nodes
     */
    public static void printRoute(String msg, String origin, String destination, List<String> nodesTraversed) {
        if (logger.isTraceEnabled()) {
            msg = msg + "Origin: " + origin + "\tDestination: " + destination + "\n";
            for (int i = 0; i < nodesTraversed.size(); i++) {
                msg += "[" + i + "]" + nodesTraversed.get(i) + ". ";
            }
            logger.trace(msg);
        }
    }

    /**
     * Prints the routing
     *
     * @param msg            some initial message
     * @param origin         origin node
     * @param destination    destination node
     * @param nodesTraversed the traversed nodes
     */
    public static void printRouteWithProperty(String msg, String origin, String destination, List<Link> nodesTraversed) {
        if (logger.isTraceEnabled()) {
            msg = msg + "Origin: " + origin + "\tDestination: " + destination + "\n";
            for (int i = 0; i < nodesTraversed.size(); i++) {
                msg += "[" + i + "]" + nodesTraversed.get(i).getCurrentNode() + "=>" + nodesTraversed.get(i).getNextNode() + ". ";
            }
            logger.trace(msg);
        }
    }
}
