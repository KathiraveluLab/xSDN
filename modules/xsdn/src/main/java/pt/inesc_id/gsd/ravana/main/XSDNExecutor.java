/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.inesc_id.gsd.ravana.constants.XSDNConstants;
import pt.inesc_id.gsd.ravana.core.XSDNEngine;
import pt.inesc_id.gsd.ravana.statistics.KnowledgeBase;
import pt.inesc_id.gsd.ravana.util.XSDNUtil;

/**
 * A sample execution of XSDN.
 *
 * <p>Supports the following CLI system properties:
 * <ul>
 *   <li>{@code -Dalgo=AdaptiveRoute|RandomRoute} — routing algorithm to use (default: RandomRoute)</li>
 *   <li>{@code -Dconf=path/to/conf} — path to the configuration directory (default: conf)</li>
 *   <li>{@code -Dhealth=true} — enable HealthMonitor telemetry (default: false)</li>
 * </ul>
 *
 * <p>Example:
 * <pre>
 *   mvn exec:java -pl modules/xsdn -Dexec.mainClass="pt.inesc_id.gsd.ravana.main.XSDNExecutor" \
 *       -Dalgo=AdaptiveRoute -Dconf=conf/benchmarking -Dhealth=true
 * </pre>
 */
public class XSDNExecutor {
    private static Logger logger = LogManager.getLogger(XSDNExecutor.class.getName());

    public static void main(String argv[]) {
        long startTime = System.currentTimeMillis();

        // --- CLI flags ---
        String algo = System.getProperty("algo", "RandomRoute");
        String confDir = System.getProperty("conf", XSDNConstants.CONF_FOLDER);
        boolean healthEnabled = Boolean.parseBoolean(System.getProperty("health", "false"));

        logger.info("xSDN starting — algo={}, conf={}, health={}", algo, confDir, healthEnabled);
        System.out.println("[xSDN] algo=" + algo + "  conf=" + confDir + "  health=" + healthEnabled);

        // Override configuration directory if specified
        if (!confDir.equals(XSDNConstants.CONF_FOLDER)) {
            System.setProperty("xsdn.conf.dir", confDir);
        }

        // --- Health Monitor ---
        Initiator.setIsHealthMonitoringEnabled(healthEnabled);
        if (healthEnabled) {
            Initiator.setWaitTimeInMillis(1000);
        }

        // --- Initialize network, routes, and flows ---
        Initiator.initiateRouteList(10);
        XSDNEngine.initFlows();

        // --- Infinispan optional: activate for distributed mode ---
        KnowledgeBase.initInfinispan();

        // --- Execute simulation with selected algorithm ---
        XSDNEngine.executeSimulations(algo);

        XSDNUtil.logTotalExecTime(startTime);
    }

}
