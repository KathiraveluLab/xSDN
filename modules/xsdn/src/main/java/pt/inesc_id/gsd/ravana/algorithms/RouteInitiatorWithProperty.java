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
import pt.inesc_id.gsd.ravana.builders.FlowBuilder;
import pt.inesc_id.gsd.ravana.builders.Parser;
import pt.inesc_id.gsd.ravana.constants.XSDNConstants;
import pt.inesc_id.gsd.ravana.core.XSDNCore;
import pt.inesc_id.gsd.ravana.network.Link;

import java.io.File;
import java.util.*;

/**
 * An initiator also keeps track of a single property
 */
public class RouteInitiatorWithProperty extends XSDNCore {
    // Last element in each string is the value of the intent.
    protected static Map<String, List<String>> possibleRoutes = new HashMap<>();

    private static Logger logger = LogManager.getLogger(RouteInitiatorWithProperty.class.getName());
    protected static final Set<String> flows = getXSDNFlows().keySet();


    /**
     * Random Walk the flow from the origin to destination
     *
     * @param flowId, the flow id.
     * @param propertyName,      name of the property
     * @param isDecrementIntent, intent to be decrement, or increment
     */
    public static void initialRoute(String flowId, String propertyName, boolean isDecrementIntent) {
        String origin = XSDNCore.getOrigin(flowId);
        String destination = XSDNCore.getDestination(flowId);

        String intermediaries = "";

        List<Link> nodesTraversed = CyclesTruncatedRandomWalk.routeWithProperty(origin, destination, propertyName);

        CyclesTruncatedRandomWalk.printRouteWithProperty("[RouteInitiatorWithProperty]", origin, destination, nodesTraversed);

        String key = origin + " " + destination;

        double intent = 0;

        for (int i = 0; i < nodesTraversed.size(); i++) {
            intermediaries += nodesTraversed.get(i).getNextNode() + " ";
            if (isDecrementIntent) {
                intent += nodesTraversed.get(i).getPropertyValue();
            } else {
                intent += (1 / (nodesTraversed.get(i).getPropertyValue()));
            }
        }
        intermediaries += intent;

        List<String> routeList = new ArrayList<>();

        if (possibleRoutes.containsKey(key)) {
            routeList = possibleRoutes.get(key);
        }
        if (routeList.contains(intermediaries)) {
            if (logger.isTraceEnabled()) {
                String msg = intermediaries + " already exists as a route for " + key;
                logger.trace(msg);
            }
        } else {
            routeList.add(intermediaries);
            possibleRoutes.put(key, routeList);
            if (logger.isTraceEnabled()) {
                String msg = "Adding " + intermediaries + " as a route for " + key;
                logger.trace(msg);
            }
        }
    }


    /**
     * Initialize the initialRoute list by routing sample flows multiple times using the algorithm.
     *
     * @param times              number of flows to be routed to populate the lists.
     * @param propertyName,      name of the property
     * @param isDecrementIntent, intent to be decrement, or increment
     */
    public static void initializePossibleRoutesList(int times, String propertyName, boolean isDecrementIntent) {
        for (String flow : flows) {
            for (int i = 0; i < times; i++) {
                initialRoute(flow, propertyName, isDecrementIntent);
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
        if (possibleRoutes.containsKey(key)) {
            return possibleRoutes.get(key);
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
    }

    private static void clearInitFlows() {
        Object[] flowArray = flows.toArray();
        for (Object aFlowArray : flowArray) {
            flows.remove(aFlowArray);
        }
    }
}
