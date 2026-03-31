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
        pt.inesc_id.gsd.ravana.flow.XSDNFlow flow = xSDNFlows.get(flowId);
        String origin = flow.getOrigin();
        String destination = flow.getDestination();
        String key = getKey(flowId);
        List<String> routes = possibleRoutes.get(key);

        // SLA Intent Analysis
        String profile = flow.getProfile();
        pt.inesc_id.gsd.ravana.intents.XSDNPolicy policy = xSDNPolicies.get(profile);
        String propertyToOptimize = "time";
        boolean isMinimize = true;

        if (policy != null) {
            Map<String, String> policyMap = policy.getPolicyMap();
            if (policyMap.containsKey("property")) {
                propertyToOptimize = policyMap.get("property");
            }
            if (policyMap.containsKey("goal")) {
                isMinimize = policyMap.get("goal").equalsIgnoreCase("minimize");
            }
        }

        String chosenRoute = "";
        for (pt.inesc_id.gsd.ravana.flow.Chunk chunk : flow.getChunks().values()) {
            String bestRouteStr = null;

            // Fetch SLA-aware historical best route per chunk
            FlowStatistics bestStat = pt.inesc_id.gsd.ravana.statistics.KnowledgeBase.getBestHistoricalRoute(
                    origin, destination, propertyToOptimize, isMinimize);

            if (bestStat != null) {
                bestRouteStr = bestStat.getStringProperty("route");
            }

            String route;
            if (bestRouteStr != null && !bestRouteStr.isEmpty()) {
                route = bestRouteStr;
            } else {
                // Fallback to random route
                int random = RandomUtil.randomLessThanMax(routes.size() - 1);
                route = routes.get(random);
            }
            chosenRoute = route;

            String[] temp = route.isEmpty() ? new String[0] : route.split(" ");
            int length = temp.length + 2;
            String[] nodes = new String[length];
            nodes[0] = origin;
            System.arraycopy(temp, 0, nodes, 1, temp.length);
            nodes[length - 1] = destination;

            chunk.setDesignatedRoute(nodes);
            flow.setDesignatedRouteOnlyToFlow(nodes);
        }

        if (logger.isInfoEnabled()) {
            logger.info("AdaptiveRoute: Intent-based evaluation (" + propertyToOptimize + ") completed for flow " + flowId + ". Starting routing...");
        }

        double time = flow.startRouting("AdaptiveRoute");
        flow.setCompleted(true);

        // Feedback loop: Record comprehensive SLA statistics
        FlowStatistics stats = new FlowStatistics(flowId, origin, destination, time);
        stats.addStringProperty("route", chosenRoute);
        stats.addProperty("energy", flow.getTotalProperty("energy"));
        stats.addProperty("cost", flow.getTotalProperty("cost"));
        
        // Record speed as average speed across chunks if needed, 
        // or just use the speed of the first link of the first chunk as a proxy
        double totalSpeed = 0;
        int chunkCount = flow.getChunks().size();
        for (pt.inesc_id.gsd.ravana.flow.Chunk c : flow.getChunks().values()) {
            String[] r = c.getDesignatedRoute();
            if (r != null && r.length > 1) {
                totalSpeed += XSDNCore.getSpeedOfLink(r[0], r[1]);
            }
        }
        stats.addProperty("speed", chunkCount > 0 ? totalSpeed / chunkCount : 0);

        pt.inesc_id.gsd.ravana.statistics.KnowledgeBase.addFlowStatistics(flowId, stats);
        
        if (logger.isInfoEnabled()) {
            logger.info("Routing completed. Flow " + flowId + " Stats -> Time: " + time + ", Energy: " + stats.getProperty("energy") + ", Goal: " + propertyToOptimize);
        }
    }
}
