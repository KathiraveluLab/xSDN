package pt.inesc_id.gsd.ravana.util;

import java.util.Arrays;

/**
 * Utility class for statistical math operations.
 */
public class MathUtils {

    public static double getMean(double[] data) {
        if (data == null || data.length == 0) return 0.0;
        double sum = 0.0;
        for (double a : data) {
            sum += a;
        }
        return sum / data.length;
    }

    public static double getVariance(double[] data) {
        if (data == null || data.length == 0) return 0.0;
        double mean = getMean(data);
        double temp = 0;
        for (double a : data) {
            temp += (a - mean) * (a - mean);
        }
        return temp / data.length;
    }

    public static double getStdDev(double[] data) {
        return Math.sqrt(getVariance(data));
    }

    public static double getPercentile(double[] data, double percentile) {
        if (data == null || data.length == 0) return 0.0;
        double[] sorted = data.clone();
        Arrays.sort(sorted);
        int index = (int) Math.ceil(percentile / 100.0 * sorted.length) - 1;
        if (index < 0) index = 0;
        if (index >= sorted.length) index = sorted.length - 1;
        return sorted[index];
    }
}
