/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.infinispan;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import pt.inesc_id.gsd.ravana.constants.InfinispanConstants;

/**
 * Core class for Infinispan integration
 */
public class InfCore {
    private static Logger logger = LogManager.getLogger(InfCore.class.getName());

    private static InfCore infiniCore = null;
    protected static Cache<String, String> defaultCache;
    protected static Cache<String, pt.inesc_id.gsd.ravana.statistics.FlowStatistics> statisticsCache;

    protected static Cache<String, pt.inesc_id.gsd.ravana.network.XSDNNode> nodesCache;
    protected static Cache<String, pt.inesc_id.gsd.ravana.flow.XSDNFlow> flowsCache;
    protected static Cache<String, pt.inesc_id.gsd.ravana.policy.XSDNPolicy> policiesCache;
    protected static Cache<java.lang.String, java.util.List<java.lang.String>> routesCache;

    public Cache<String, pt.inesc_id.gsd.ravana.network.XSDNNode> getNodesCache() { return nodesCache; }
    public Cache<String, pt.inesc_id.gsd.ravana.flow.XSDNFlow> getFlowsCache() { return flowsCache; }
    public Cache<String, pt.inesc_id.gsd.ravana.policy.XSDNPolicy> getPoliciesCache() { return policiesCache; }
    public Cache<java.lang.String, java.util.List<java.lang.String>> getRoutesCache() { return routesCache; }

    public Cache<String, pt.inesc_id.gsd.ravana.statistics.FlowStatistics> getStatisticsCache() {
        return statisticsCache;
    }

    public Cache<String, String> getDefaultCache() {
        return defaultCache;
    }

    /**
     * Singleton. Prevents initialization from outside the class.
     *
     * @throws java.io.IOException, if getting the cache failed.
     */
    protected InfCore() throws IOException {
        DefaultCacheManager manager = new DefaultCacheManager(InfinispanConstants.INFINISPAN_CONFIG_FILE);
        defaultCache = manager.getCache(InfinispanConstants.TRANSACTIONAL_CACHE);
        statisticsCache = manager.getCache("statistics");
        nodesCache = manager.getCache("nodes");
        flowsCache = manager.getCache("flows");
        policiesCache = manager.getCache("policies");
        routesCache = manager.getCache("routes");
    }

    /**
     * Initializes Infinispan
     */
    public static InfCore getInfiniCore() {
        if (infiniCore == null) {
            try {
                infiniCore = new InfCore();
            } catch (IOException e) {
                logger.info("Exception when trying to initialize Infinispan", e);
            }
        }
        return infiniCore;
    }
}
