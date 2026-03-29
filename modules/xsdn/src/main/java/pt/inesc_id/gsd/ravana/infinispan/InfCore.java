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
