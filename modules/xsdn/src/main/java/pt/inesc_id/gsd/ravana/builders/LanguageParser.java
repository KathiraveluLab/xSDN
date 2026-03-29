/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.builders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.inesc_id.gsd.ravana.core.XSDNCore;
import pt.inesc_id.gsd.ravana.flow.Chunk;
import pt.inesc_id.gsd.ravana.flow.XSDNFlow;
import pt.inesc_id.gsd.ravana.util.RandomUtil;
import pt.inesc_id.gsd.ravana.util.XSDNUtil;

/**
 * Parses the xSDN representation of flows.
 */
public class LanguageParser {
    private static Logger logger = LogManager.getLogger(LanguageParser.class.getName());
    private static boolean isWaitStrictlyOrdered;
    private static double waitTime = 0;
    private static XSDNFlow xsdnFlow;

    /**
     * Builds xSDN flows
     *
     * @param idVal       key for the flow
     * @param start       start time
     * @param chunks      array of chunks
     * @param origin      origin node
     * @param destination target node
     * @param chunkArray  array of chunks of the flow.
     */
    public static void buildxSDNFlows(String idVal, double start, String chunks, String origin,
                                      String destination, String[] chunkArray) {
        // Initialize a XSDN Flow
        xsdnFlow = new XSDNFlow(start, origin, destination);

        if (chunkArray.length > 0) {
            isWaitStrictlyOrdered = LanguageUtil.isWaitStrictlyOrdered(chunkArray[0]);
            xsdnFlow.setWaitStrictlyOrdered(isWaitStrictlyOrdered);

            for (String aChunkArray : chunkArray) {
                // Numeral cases
                if (XSDNUtil.isDouble(aChunkArray)) {
                    // Case 1:
                    addChunk(Double.parseDouble(aChunkArray));
                } else if (aChunkArray.equalsIgnoreCase("R")) {
                    // Case 9:
                    addChunk(RandomUtil.generateRandomDouble());
                } else if (aChunkArray.contains("/")) {
                    // Dividing cases
                    handleDividingCases(aChunkArray);
                } else if (aChunkArray.contains("*")) {
                    // Multiplication cases
                    handleMultiplicationCases(aChunkArray);
                } else if (aChunkArray.contains("R")) {
                    // Random in a range cases.
                    handleRangedRandomCases(aChunkArray);
                } else {
                    //Just interval cases
                    handleIntervals(aChunkArray);
                }
            }
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Start: " + start + "\nChunks: " + chunks + "\nOrigin: " + origin + "\nDestination: " +
                    destination);
        }

        XSDNCore.addXSDNFlow(idVal, xsdnFlow);
    }

    /**
     * Handles the interval cases.
     *
     * @param str, the string with the D or G notation
     */
    protected static void handleIntervals(String str) {
        String truncatedLastChar = ParseUtil.getStringLastCharTruncated(str);
        char lastChar = ParseUtil.getLastCharacter(str);
        if ((XSDNUtil.isDouble(truncatedLastChar)) || (truncatedLastChar.trim().length() == 0)) {
            // Case 2, 3, 11, 12.
            waitTime = LanguageUtil.getWaitTime(truncatedLastChar);
            isWaitStrictlyOrdered = LanguageUtil.strictOrRelaxed(lastChar);
        }
    }

    /**
     * Handles the multiplication cases.
     *
     * @param str, string with the multiplication sign
     */
    protected static void handleMultiplicationCases(String str) {
        String[] parts = str.split("[*]");
        int cardinality = Integer.parseInt(parts[1].trim());
        //Case 4
        if (XSDNUtil.isDouble(parts[0])) {
            double value = initializeValueForMultiplicationCases(parts[0]);
            addMultipleChunks(parts[0], cardinality, value);
        } else if (parts[0].contains("+")) {
            // Multiplication cases with intervals.
            String[] subParts = parts[0].split("[+]");
            double value = initializeValueForMultiplicationCases(subParts[0]);
            handleIntervals(subParts[1]);
            addMultipleChunks(subParts[0], cardinality, value);
        }
    }

    /**
     * Handles the Dividing cases.
     *
     * @param str, string with the dividing sign
     */
    protected static void handleDividingCases(String str) {
        int cardinality = 1;
        String[] parts = str.split("[/]");
        double total = Double.parseDouble(parts[0].trim());

        if (XSDNUtil.isInteger(parts[1].trim())) {
            // Case 14.
            cardinality = Integer.parseInt(parts[1].trim());
        } else if (parts[1].contains("+")) {
            // Case 8. Dividing cases with intervals.
            String[] subParts = parts[1].split("[+]");
            cardinality = Integer.parseInt(subParts[0]);
            handleIntervals(subParts[1]);
        }
        double[] value = RandomUtil.divideRandomlyAndUniformly(total, cardinality);

        for (int j = 0; j < cardinality; j++) {
            addChunk(value[j]);
        }
    }

    /**
     * Handles the Random in a range cases.
     *
     * @param str, string with the R
     */
    protected static void handleRangedRandomCases(String str) {
        String[] parts = str.split("[R]");
        double min = Double.parseDouble(parts[0]);
        double max;
        int cardinality;

        if (XSDNUtil.isDouble(parts[1])) {
            // Case 15.
            max = Double.parseDouble(parts[1]);
            addChunk(RandomUtil.randomWithInaRange(min, max));
        } else if (parts[1].contains("*")) {
            String[] subParts = parts[1].split("[*]");
            cardinality = Integer.parseInt(subParts[1]);
            if (XSDNUtil.isInteger(subParts[0])) {
                // Case 16.
                addChunksForRangedRandoms(min, cardinality, subParts);
            } else {
                // Case 10.
                String[] subsub = subParts[0].split("[+]");
                handleIntervals(subsub[1]);
                addChunksForRangedRandoms(min, cardinality, subsub);
            }
        }
    }

    /**
     * Handles multiple chunk cases for ranged randoms.
     *
     * @param min,         minimum value
     * @param cardinality, how many chunks
     * @param subParts,    sub parts array.
     */
    protected static void addChunksForRangedRandoms(double min, int cardinality, String[] subParts) {
        double max;
        max = Double.parseDouble(subParts[0]);
        for (int j = 0; j < cardinality; j++) {
            addChunk(RandomUtil.randomWithInaRange(min, max));
        }
    }

    /**
     * Initializes the value for the multiplication cases.
     *
     * @param part, part of the string containing the size/value
     * @return the size of the chunk.
     */
    protected static double initializeValueForMultiplicationCases(String part) {
        if (XSDNUtil.isDouble(part.trim())) {
            return Double.parseDouble(part.trim());
        }
        return 0;
    }

    /**
     * Adds multiple chunks at once to the xsdn flow.
     *
     * @param part,        part of the string
     * @param cardinality, how many chunks
     * @param value,       size of the chunk
     */
    protected static void addMultipleChunks(String part, int cardinality, double value) {
        for (int j = 0; j < cardinality; j++) {
            // Case 13
            if (part.trim().equalsIgnoreCase("R")) {
                value = RandomUtil.generateRandomDouble();
            }
            addChunk(value);
        }
    }

    /**
     * Adds the chunk to the flow, finally.
     *
     * @param value, the size of the chunk.
     */
    protected static void addChunk(double value) {
        int id = xsdnFlow.getChunkIdTracking();
        Chunk chunk = new Chunk(id, value, isWaitStrictlyOrdered, waitTime);
        xsdnFlow.addChunk(id, chunk);
        xsdnFlow.incrementChunkIdTracking();
    }
}
