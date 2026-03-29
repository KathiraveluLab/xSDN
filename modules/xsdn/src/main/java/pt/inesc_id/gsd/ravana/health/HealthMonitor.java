/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.health;

import com.sun.management.OperatingSystemMXBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;

/**
 * Health Monitor Thread.
 */
public class HealthMonitor implements Runnable {
    private static Logger logger = LogManager.getLogger(HealthMonitor.class.getName());

    private Runtime runtime;
    private OperatingSystemMXBean osMxBean;
    private long memoryFree;
    private long memoryTotal;
    private long memoryUsed;
    private long memoryMax;
    private double memoryUsedOfTotalPercentage;
    private double memoryUsedOfMaxPercentage;
    private double systemCpuLoad;
    private double processCpuLoad;
    private double systemLoadAverage; // -1.0 in windows
    private static String healthLogs = "";

    private static int waitTimeInMillis;

    public static void setWaitTimeInMillis(int waitTimeInMillis) {
        HealthMonitor.waitTimeInMillis = waitTimeInMillis;
    }

    public HealthMonitor() {
        runtime = Runtime.getRuntime();
        osMxBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    }

    public static void printHealthLogs() {
        logger.debug(healthLogs);
    }

    /**
     * Periodically initiate the current values of the system parameters
     */
    private void init() {
        memoryFree = runtime.freeMemory();
        memoryTotal = runtime.totalMemory();
        memoryUsed = memoryTotal - memoryFree;
        memoryMax = runtime.maxMemory();
        memoryUsedOfTotalPercentage = 100d * memoryUsed / memoryTotal;
        memoryUsedOfMaxPercentage = 100d * memoryUsed / memoryMax;
        systemCpuLoad = osMxBean.getSystemCpuLoad(); // get(osMxBean, "getSystemCpuLoad", -1L);
        processCpuLoad = osMxBean.getProcessCpuLoad();
        systemLoadAverage = osMxBean.getSystemLoadAverage();
        healthLogs += "[HealthMonitor]: Memory Used of Total, as Percentage: " + memoryUsedOfTotalPercentage +
                ". Memory Used of Maximum, as Percentage: " + memoryUsedOfMaxPercentage +
                ". System CPU Load: " + systemCpuLoad + ". Process CPU Load: " + processCpuLoad +
                ". System Load Average: " + systemLoadAverage + ".\n";
    }

    /**
     * Gets the health logs that are stored.
     *
     * @return health logs
     */
    public static String getHealthLogs() {
        return healthLogs;
    }

    public long getMemoryFree() {
        return memoryFree;
    }

    public long getMemoryTotal() {
        return memoryTotal;
    }

    public long getMemoryUsed() {
        return memoryUsed;
    }

    public long getMemoryMax() {
        return memoryMax;
    }

    public double getMemoryUsedOfTotalPercentage() {
        return memoryUsedOfTotalPercentage;
    }

    public double getMemoryUsedOfMaxPercentage() {
        return memoryUsedOfMaxPercentage;
    }

    public double getSystemCpuLoad() {
        return systemCpuLoad;
    }

    public double getProcessCpuLoad() {
        return processCpuLoad;
    }

    public double getSystemLoadAverage() {
        return systemLoadAverage;
    }


    @Override
    public void run() {
        while (true) {
            init();
            try {
                Thread.sleep(waitTimeInMillis);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
