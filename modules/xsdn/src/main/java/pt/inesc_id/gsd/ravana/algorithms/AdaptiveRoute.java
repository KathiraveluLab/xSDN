package pt.inesc_id.gsd.ravana.algorithms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.inesc_id.gsd.ravana.core.XSDNCore;
import pt.inesc_id.gsd.ravana.statistics.FlowStatistics;
import pt.inesc_id.gsd.ravana.util.RandomUtil;

import java.util.List;
import java.util.Map;

/**
 * Iterates through historical knowledge base to make optimal adaptive routes.
 */
public class AdaptiveRoute extends RouteInitiator {
    private static Logger logger = LogManager.getLogger(AdaptiveRoute.class.getName());

    /**
     * Perform an adaptive routing of the flow
     *
     * @param flowId, id of the flow.
     */
    public static void route(String flowId) {
        String origin = xSDNFlows.get(flowId).getOrigin();
        String destination = xSDNFlows.get(flowId).getDestination();
        String key = getKey(flowId);
        List<String> routes = possibleRoutes.get(key);

        String bestRouteStr = null;
        double bestTime = Double.MAX_VALUE;

        // Query KnowledgeBase for historical routes matching this origin and destination
        Map<String, FlowStatistics> kb = XSDNCore.getKnowledgeBase();
        for (FlowStatistics stats : kb.values()) {
            if (stats.getOrigin().equals(origin) && stats.getDestination().equals(destination)) {
                if (stats.getTimeTakenEnroute() < bestTime) {
                    bestTime = stats.getTimeTakenEnroute();
                    bestRouteStr = stats.getProperty("route");
                }
            }
        }

        String route;
        if (bestRouteStr != null && !bestRouteStr.isEmpty()) {
            route = bestRouteStr;
            if (logger.isInfoEnabled()) {
                logger.info("AdaptiveRoute: Found historical best route for flow " + flowId + " (" + origin + "=>" + destination + ") with time " + bestTime + ". Route: " + route);
            }
        } else {
            // Fallback to random route
            int random = RandomUtil.randomLessThanMax(routes.size() - 1);
            route = routes.get(random);
            if (logger.isInfoEnabled()) {
                logger.info("AdaptiveRoute: No history found. Randomly chosen route for " + flowId + " (" + origin + "=>" + destination + ") is: " + route);
            }
        }

        String[] temp = route.isEmpty() ? new String[0] : route.split(" ");
        int length = temp.length + 2;
        String[] nodes = new String[length];
        nodes[0] = origin;
        System.arraycopy(temp, 0, nodes, 1, temp.length);
        nodes[length - 1] = destination;

        xSDNFlows.get(flowId).setDesignatedRoute(nodes);
        double time = xSDNFlows.get(flowId).startRouting("AdaptiveRoute");
        xSDNFlows.get(flowId).setCompleted(true);
        if (logger.isInfoEnabled()) {
            logger.info("Routing completed at time: " + time);
        }

        // Feedback loop: Record statistics
        FlowStatistics stats = new FlowStatistics(flowId, origin, destination, time);
        stats.addProperty("route", route);
        kb.put(flowId, stats);
    }
}
