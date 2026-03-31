package pt.inesc_id.gsd.ravana.statistics;

import java.util.HashMap;
import java.util.Map;

/**
 * Distributed shared knowledge base simulating the cache
 * (e.g., Infinispan) to store flow statistics for adaptive routing decisions as
 * described in 2015SDS.
 */
public class KnowledgeBase {

    private static Map<String, FlowStatistics> flowStatisticsMap = new HashMap<>();
    private static Map<String, FlowStatistics> bestRouteStatisticsMap = new HashMap<>();

    public static void initInfinispan() {
        try {
            pt.inesc_id.gsd.ravana.infinispan.InfCore infiniCore = pt.inesc_id.gsd.ravana.infinispan.InfCore.getInfiniCore();
            flowStatisticsMap = infiniCore.getStatisticsCache();
            bestRouteStatisticsMap = infiniCore.getBestRoutesCache();
        } catch (Exception e) {
            System.err.println("Could not initialize Infinispan for KnowledgeBase, fallback to local map: " + e.getMessage());
        }
    }

    public static void addFlowStatistics(String flowId, FlowStatistics stats) {
        flowStatisticsMap.put(flowId, stats);
        String origin = stats.getOrigin();
        String destination = stats.getDestination();
        String odKeyBase = origin + " " + destination + " ";

        // Update indices for all relevant SLA properties
        updateBestRoute(odKeyBase + "time", stats, "time", true);
        updateBestRoute(odKeyBase + "energy", stats, "energy", true);
        updateBestRoute(odKeyBase + "cost", stats, "cost", true);
        updateBestRoute(odKeyBase + "speed", stats, "speed", false); // Maximize bandwidth
    }

    private static void updateBestRoute(String fullKey, FlowStatistics stats, String propertyName, boolean isMinimize) {
        FlowStatistics currentBest = bestRouteStatisticsMap.get(fullKey);
        double newValue = propertyName.equals("time") ? stats.getTimeTakenEnroute() : stats.getProperty(propertyName);
        
        if (currentBest == null) {
            bestRouteStatisticsMap.put(fullKey, stats);
            return;
        }

        double currentValue = propertyName.equals("time") ? currentBest.getTimeTakenEnroute() : currentBest.getProperty(propertyName);

        if (isMinimize) {
            if (newValue < currentValue) {
                bestRouteStatisticsMap.put(fullKey, stats);
            }
        } else {
            if (newValue > currentValue) {
                bestRouteStatisticsMap.put(fullKey, stats);
            }
        }
    }

    public static FlowStatistics getBestHistoricalRoute(String origin, String destination) {
        return getBestHistoricalRoute(origin, destination, "time", true);
    }

    public static FlowStatistics getBestHistoricalRoute(String origin, String destination, String propertyName, boolean isMinimize) {
        return bestRouteStatisticsMap.get(origin + " " + destination + " " + propertyName);
    }

    public static Map<String, FlowStatistics> getAllStatistics() {
        return flowStatisticsMap;
    }

    public static FlowStatistics getFlowStatistics(String flowId) {
        return flowStatisticsMap.get(flowId);
    }
}
