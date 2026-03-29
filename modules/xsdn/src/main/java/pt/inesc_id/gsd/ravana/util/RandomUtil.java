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

import java.util.Random;
import java.util.UUID;

/**
 * The utility class for generation of Random Data Structures.
 */
public class RandomUtil {
    private static Logger logger = LogManager.getLogger(RandomUtil.class.getName());

    private static final String alphaNumerals = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static Random random = new Random();

    /**
     * Generates a random string
     *
     * @param length length of the string
     * @return the string
     */
    public static String generateRandomString(int length) {
        if (logger.isDebugEnabled()) {
            logger.debug("Generating random string with length: " + length);
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(generateRandomCharacter());
        return sb.toString();
    }

    /**
     * Generates a random long
     *
     * @return the integer
     */
    public static long generateRandomLong() {
        return UUID.randomUUID().getLeastSignificantBits();
    }

    /**
     * Generates a random integer
     *
     * @return the integer
     */
    public static int generateRandomInteger() {
        return random.nextInt();
    }

    /**
     * Generates a random double
     *
     * @return the double
     */
    public static double generateRandomDouble() {
        return random.nextDouble();
    }

    /**
     * Generates a random character
     *
     * @return the character
     */
    public static char generateRandomCharacter() {
        return alphaNumerals.charAt(random.nextInt(alphaNumerals.length()));
    }

    /**
     * Divides a given number into a specified number of random parts uniformly.
     *
     * @param number, the given large number
     * @param part,   the number of particles
     * @return the double array of particles
     * @see <url>http://stackoverflow.com/questions/9891457/dividing-a-number-into-m-parts-uniformly-randomly</url>
     */
    public static double[] divideRandomlyAndUniformly(double number, int part) {
        double particles[] = new double[part];

        double mean = number / part;
        double sum = 0.0;

        for (int i = 0; i < part / 2; i++) {
            particles[i] = random.nextDouble() * mean;

            particles[part - i - 1] = mean + random.nextDouble() * mean;

            sum += particles[i] + particles[part - i - 1];
        }
        particles[(int) Math.ceil(part / 2)] = particles[(int) Math.ceil(part / 2)] + number - sum;

        return particles;
    }

    /**
     * Returns a random number within a given range
     *
     * @param min, minimum limit for the number
     * @param max, maximum limit
     * @return the random number
     */
    public static int randomWithInaRange(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    /**
     * Returns a random number within a max limit
     *
     * @param max, maximum limit
     * @return the random number
     */
    public static int randomLessThanMax(int max) {
        return random.nextInt(max + 1);
    }

    /**
     * Returns a random double within a given range
     *
     * @param min, minimum limit for the number
     * @param max, maximum limit
     * @return the random number
     */
    public static double randomWithInaRange(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}
