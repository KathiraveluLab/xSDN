/*
 * Title:        Cloud2Sim
 * Description:  Distributed and Concurrent Cloud Simulation
 *                Toolkit for Modeling and Simulation
 *                of Clouds - Enhanced version of CloudSim.
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */

package pt.inesc_id.gsd.ravana.infinispan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.infinispan.Cache;

/**
 * Starts Infinispan instance.
 */
public class InfInitiator {
    private static Logger logger = LogManager.getLogger(InfInitiator.class.getName());

    public static void main(String[] args) {
        InfCore infiniCore = InfCore.getInfiniCore();
        @SuppressWarnings("unused")
        Cache defaultCache = infiniCore.getDefaultCache();
        logger.info("Infinispan Initiator instance started..");
    }
}
