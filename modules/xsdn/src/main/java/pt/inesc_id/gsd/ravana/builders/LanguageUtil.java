/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.builders;

/**
 * Utility methods for language representation.
 */
public class LanguageUtil {
    /**
     * is the order strict
     * @param lastChar the last char
     * @return true, if the order is strict
     */
    public static boolean strictOrRelaxed(char lastChar) {
        // Case 2:
        if (lastChar == 'D' || lastChar == 'd') {
            return false;
        }
        // Case 3:
        else if (lastChar == 'G' || lastChar == 'g') {
            return true;
        }
        // true, by default.
        return true;
    }

    /**
     * Is the order strict
     * @param str the string
     * @return true, if the order is strict
     */
    public static boolean isWaitStrictlyOrdered(String str) {
        return !str.equalsIgnoreCase("D");
    }

    /**
     * Gets the wait time from the sub string
     * @param truncatedLastChar, string after truncating the last character
     * @return wait time.
     */
    public static double getWaitTime(String truncatedLastChar) {
        if (truncatedLastChar.trim().length() == 0) {
            return 0;
        } else {
            return Double.parseDouble(truncatedLastChar);
        }
    }
}
