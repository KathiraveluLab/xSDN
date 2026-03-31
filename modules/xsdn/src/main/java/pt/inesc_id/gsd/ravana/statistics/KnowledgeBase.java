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

    public static void initInfinispan() {
        try {
            pt.inesc_id.gsd.ravana.infinispan.InfCore infiniCore = pt.inesc_id.gsd.ravana.infinispan.InfCore.getInfiniCore();
            flowStatisticsMap = infiniCore.getStatisticsCache();
        } catch (Exception e) {
            System.err.println("Could not initialize Infinispan for KnowledgeBase, fallback to local map: " + e.getMessage());
        }
    }

    public static void addFlowStatistics(String flowId, FlowStatistics stats) {
        flowStatisticsMap.put(flowId, stats);
    }

    public static FlowStatistics getFlowStatistics(String flowId) {
        return flowStatisticsMap.get(flowId);
    }

    public static Map<String, FlowStatistics> getAllStatistics() {
        return flowStatisticsMap;
    }
}
