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
import pt.inesc_id.gsd.ravana.algorithms.RouteInitiator;
import pt.inesc_id.gsd.ravana.util.RandomUtil;

import java.util.List;

/**
 * Route using the path randomly chosen from the list of possible routes.
 */
public class InfRandomRoute extends InfRouteInitiator {
    private static Logger logger = LogManager.getLogger(InfRandomRoute.class.getName());


    /**
     * Perform a routing of the flow, using the algorithm
     *
     * @param flowId, id of the flow.
     */
    public static void route(String flowId) {
        String key = getKey(flowId);
        List<String> routes = (List<String>) defaultCache.get(key);
        int random = RandomUtil.randomLessThanMax(routes.size() - 1);
        if (logger.isDebugEnabled()) {
            logger.debug(flowId + ": " + getOriginAndDestination(flowId) + ": " + routes.get(random));
        }
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
