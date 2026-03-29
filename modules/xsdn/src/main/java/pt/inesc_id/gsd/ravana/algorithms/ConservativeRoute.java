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

import java.util.List;

/**
 * Routing in a conservative manner. For example, "Save Money"
 */
public class ConservativeRoute extends RouteInitiatorWithProperty {
    private static Logger logger = LogManager.getLogger(ConservativeRoute.class.getName());

    /**
     * Perform a routing of the flow, using the algorithm
     *
     * @param flowId,            id of the flow.
     * @param isDecrementIntent, is it to be decremented
     */
    public static void route(String flowId, boolean isDecrementIntent) {
        String key = getKey(flowId);
        List<String> routes = possibleRoutes.get(key);

        String[] rWithIntent = routes.get(0).split(" ");

        double currentIntent;
        double trackingIntent = Double.parseDouble(rWithIntent[rWithIntent.length - 1]);
        int id = 0;


        for (int i = 0; i < routes.size(); i++) {
            rWithIntent = routes.get(i).split(" ");
            currentIntent = Double.parseDouble(rWithIntent[rWithIntent.length - 1]);
            if (isDecrementIntent) {
                if (currentIntent < trackingIntent) {
                    trackingIntent = currentIntent;
                    id = i;
                }
            } else {
                if (currentIntent > trackingIntent) {
                    trackingIntent = currentIntent;
                    id = i;
                }
            }
        }

        logger.debug(flowId + ": " + getOriginAndDestination(flowId) + ": " + routes.get(id));
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
