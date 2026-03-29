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
import pt.inesc_id.gsd.ravana.util.RandomUtil;

import java.util.List;

/**
 * Route using the path randomly chosen from the list of possible routes.
 */
public class RandomRoute extends RouteInitiator {
    private static Logger logger = LogManager.getLogger(RandomRoute.class.getName());


    /**
     * Trace a routing of the flow, using the algorithm
     *
     * @param flowId, id of the flow.
     */
    public static void traceRoute(String flowId) {
        String key = getKey(flowId);
        List<String> routes = possibleRoutes.get(key);
        int random = RandomUtil.randomLessThanMax(routes.size() - 1);
        if (logger.isDebugEnabled()) {
            logger.debug(flowId + ": " + getOriginAndDestination(flowId) + ": " + routes.get(random));
        }
    }

    /**
     * Perform a routing of the flow, using the algorithm
     *
     * @param flowId, id of the flow.
     */
    public static void route(String flowId) {
        String key = getKey(flowId);
        List<String> routes = possibleRoutes.get(key);
        int random = RandomUtil.randomLessThanMax(routes.size() - 1);
        String route = routes.get(random);
        if (logger.isInfoEnabled()) {
            logger.info("Chosen route for flow " + flowId + "(" + xSDNFlows.get(flowId).getOrigin() + "=>" +
                    xSDNFlows.get(flowId).getDestination()+ ") is: " + route);
        }

        String[] temp = route.split(" ");
        int length = temp.length + 2;
        String[] nodes = new String[length];
        nodes[0] = xSDNFlows.get(flowId).getOrigin();

        System.arraycopy(temp, 0, nodes, 1, length - 1 - 1);

        nodes[length - 1] = xSDNFlows.get(flowId).getDestination();
        xSDNFlows.get(flowId).setDesignatedRoute(nodes);
        double time = xSDNFlows.get(flowId).startRouting("RandomRoute");
        logger.info("Routing completed at time: " + time);
    }

    protected static String getKey(String flowId) {
        String origin = xSDNFlows.get(flowId).getOrigin();
        String destination = xSDNFlows.get(flowId).getDestination();
        return origin + " " + destination;
    }

    protected static String getOriginAndDestination(String flowId) {
        String origin = xSDNFlows.get(flowId).getOrigin();
        String destination = xSDNFlows.get(flowId).getDestination();
        return origin + "=>" + destination;
    }
}
