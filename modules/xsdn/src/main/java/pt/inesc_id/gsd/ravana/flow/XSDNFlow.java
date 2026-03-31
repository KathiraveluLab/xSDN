/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.flow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * XSDN representation of a Flow
 */
public class XSDNFlow extends Flow {
    private static Logger logger = LogManager.getLogger(XSDNFlow.class.getName());

    private int chunkIdTracking = 0;

    private Map<Integer, Chunk> chunks;
    private int numberOfChunks;
    private String parentXSDNFlowID;
    private boolean isCompleted;
    private boolean isWaitStrictlyOrdered = false;

    private boolean isPartitioned = false;
    private double timeTakenEnroute = 0;
    private String[] designatedRoute;
    private String profile = "time"; // Default profile

    public XSDNFlow(double startTime, String origin, String destination) {
        super(startTime, origin, destination);
        chunks = new HashMap<>();
        isCompleted = false;
    }

    public Map<Integer, Chunk> getChunks() {
        return chunks;
    }

    public int getNumberOfChunks() {
        return numberOfChunks;
    }

    public String getParentXSDNFlowID() {
        return parentXSDNFlowID;
    }

    public void addChunk(int chunkId, Chunk chunk) {
        chunks.put(chunkId, chunk);
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public boolean isPartitioned() {
        return isPartitioned;
    }

    public void setPartitioned(boolean isPartitioned) {
        this.isPartitioned = isPartitioned;
    }

    public double getTimeTakenEnroute() {
        return timeTakenEnroute;
    }

    public void setTimeTakenEnroute(double timeTakenEnroute) {
        this.timeTakenEnroute = timeTakenEnroute;
    }

    public void addTimeTakenEnroute(double time) {
        this.timeTakenEnroute += time;
    }

    /**
     * Gets the total value for any given property consumed by the entire flow.
     *
     * @param propertyName, name of the property
     * @return the total value.
     */
    public double getTotalProperty(String propertyName) {
        double value = 0;
        for (int chunkId : chunks.keySet()) {
            value += chunks.get(chunkId).getProperty(propertyName);
        }
        return value;
    }

    public String[] getDesignatedRoute() {
        return designatedRoute;
    }

    /**
     * Add route for all the chunks in the flow
     *
     * @param designatedRoute, the route
     */
    public void setDesignatedRoute(String[] designatedRoute) {
        this.designatedRoute = designatedRoute;
        for (Map.Entry entry : chunks.entrySet()) {
            ((Chunk) entry.getValue()).setDesignatedRoute(designatedRoute);
        }
    }

    public void setDesignatedRouteOnlyToFlow(String[] designatedRoute) {
        this.designatedRoute = designatedRoute;

    }

    /**
     * Start traversing the route with the chunks
     */
    public double startRouting(String routingAlgorithm) {
        numberOfChunks = chunks.size();
        double totalTime = 0;
        double reachTime;
        if (routingAlgorithm.equalsIgnoreCase("RandomRoute") || routingAlgorithm.equalsIgnoreCase("AdaptiveRoute")) {
            if (!isWaitStrictlyOrdered) {
                for (int i = 1; i < chunks.size(); i++) {
                    double igniteTime = chunks.get(i - 1).getStartTime();
                    chunks.get(i).setIgniteTime(igniteTime);
                    if(logger.isTraceEnabled()) {
                        logger.trace("Setting the ignite time for chunk " + i + " : " + igniteTime);
                    }
                }
                for (int i = 0; i < chunks.size(); i++) {
                    reachTime = chunks.get(i).getTimeWhenReachingDestination();
                    if (reachTime > totalTime) {
                        totalTime = reachTime;
                    }
                    // Accumulate SLA properties for the chunk across its assigned route
                    chunks.get(i).addMultiplicationProperty("energy");
                    chunks.get(i).addMultiplicationProperty("cost");
                }
            }
        } else {
            logger.warn(routingAlgorithm + " is not implemented");
        }
        return totalTime;
    }

    public boolean isWaitStrictlyOrdered() {
        return isWaitStrictlyOrdered;
    }

    public void setWaitStrictlyOrdered(boolean isWaitStrictlyOrdered) {
        this.isWaitStrictlyOrdered = isWaitStrictlyOrdered;
    }

    public int getChunkIdTracking() {
        return chunkIdTracking;
    }

    public int incrementChunkIdTracking() {
        this.chunkIdTracking++;
        return this.chunkIdTracking;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
