/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Common Utility methods for xSDN
 */
public class XSDNUtil {
    private static Logger logger = LogManager.getLogger(XSDNUtil.class.getName());


    /**
     * Check if integer
     *
     * @param val, input value
     * @return true, if an integer
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean isInteger(String val) {
        try {
            Integer.parseInt(val);
        } catch (NumberFormatException ignored) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    /**
     * Check if double
     *
     * @param val, input value
     * @return true, if a double
     */
    public static boolean isDouble(String val) {
        return val.matches("[-+]?[0-9]*\\.?[0-9]+");
    }

    /**
     * Final logs, marking the termination of simulation.
     */
    public static void logTotalExecTime(long startTime) {
        long endTime = System.currentTimeMillis();
        double totalTimeTaken = (endTime - startTime) / 1000.0;
        logger.info("The total time taken for the execution: " + totalTimeTaken + " s.");
    }
}
