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

import java.util.*;

/**
 * Random walk with cycles truncated.
 */
public class CyclesTruncatedRandomWalk extends RandomWalk {
    private static Logger logger = LogManager.getLogger(CyclesTruncatedRandomWalk.class.getName());


    /**
     * Random Walk (cycles truncated) the flow from the origin to destination
     *
     * @param flowId, the flow id.
     */
    public static void route(String flowId) {
        String origin = XSDNCore.getOrigin(flowId);
        String destination = XSDNCore.getDestination(flowId);
        List<String> nodesTraversed = route(origin, destination);

        printRoute("[CyclesTruncatedRW]", origin, destination, nodesTraversed);
    }

    /**
     * Random Walk (cycles truncated) the flow from the origin to destination
     *
     * @param origin,      the origin node.
     * @param destination, the destination node
     */
    public static List<String> route(String origin, String destination) {
        String current = origin;
        List<String> nodesTraversed = new ArrayList<>();
        nodesTraversed.add(current);

        int maxHops = 1000;
        int hops = 0;

        while (!current.equalsIgnoreCase(destination) && hops < maxHops) {
            current = getNextNode(current);
            hops++;
            if (nodesTraversed.contains(current)) {
                // truncate the cycle
                nodesTraversed = nodesTraversed.subList(0, nodesTraversed.indexOf(current) + 1);
            } else {
                nodesTraversed.add(current);
            }
        }
        return nodesTraversed;
    }

    /**
     * Random Walk (cycles truncated) the flow from the origin to destination
     *
     * @param origin,      the origin node.
     * @param destination, the destination node
     */
    public static List<Link> routeWithProperty(String origin, String destination, String propertyName) {
        String current = origin;

        List<String> nodesTraversed = new ArrayList<>();
        nodesTraversed.add(current);

        Link link;
        List<Link> linksTraversed = new ArrayList<>();

        int maxHops = 1000;
        int hops = 0;

        while (!current.equalsIgnoreCase(destination) && hops < maxHops) {
            link = getNextNodeWithProperty(current, propertyName);
            current = link.getNextNode();
            hops++;

            if (nodesTraversed.contains(current)) {
                // truncate the cycle
                int index = nodesTraversed.indexOf(current);
                nodesTraversed = nodesTraversed.subList(0, index+1);
                linksTraversed = linksTraversed.subList(0, index);
            } else {
                nodesTraversed.add(current);
                linksTraversed.add(link);
            }
            current = link.getNextNode();
        }
        return linksTraversed;
    }

    /**
     * Random Walk (cycles truncated) the flow from the origin to destination
     *
     * @param flowId,      the flow id.
     * @param propertyName, the name of the property
     */
    public static void routeWithProperty(String flowId, String propertyName) {
        String origin = XSDNCore.getOrigin(flowId);
        String destination = XSDNCore.getDestination(flowId);
        List<Link> nodesTraversed = routeWithProperty(origin, destination, propertyName);

        printRouteWithProperty("[CyclesTruncatedRW]", origin, destination, nodesTraversed);
    }


    /**
     * Random Walk (cycles truncated) the flow from the origin to destination
     *
     * @param flowId,      the flow id.
     */
    public static void routeWithProperties(String flowId) {
        String origin = XSDNCore.getOrigin(flowId);
        String destination = XSDNCore.getDestination(flowId);
        List<Link> nodesTraversed = routeWithProperties(origin, destination);

        printRouteWithProperty("[CyclesTruncatedRW]", origin, destination, nodesTraversed);
    }


    /**
     * Random Walk (cycles truncated) the flow from the origin to destination
     *
     * @param origin,      the origin node.
     * @param destination, the destination node
     */
    public static List<Link> routeWithProperties(String origin, String destination) {
        String current = origin;
        List<String> nodesTraversed = new ArrayList<>();
        nodesTraversed.add(current);

        Link link;
        List<Link> linksTraversed = new ArrayList<>();

        int maxHops = 1000;
        int hops = 0;

        while (!current.equalsIgnoreCase(destination) && hops < maxHops) {
            link = getNextNodeWithProperties(current);
            current = getNextNode(current);
            hops++;

            if (nodesTraversed.contains(current)) {
                // truncate the cycle
                int index = nodesTraversed.indexOf(current);
                nodesTraversed = nodesTraversed.subList(0, index+1);
                linksTraversed = linksTraversed.subList(0, index);
            } else {
                nodesTraversed.add(current);
                linksTraversed.add(link);
            }
        }
        return linksTraversed;
    }
}
