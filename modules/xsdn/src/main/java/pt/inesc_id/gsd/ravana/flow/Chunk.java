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
import pt.inesc_id.gsd.ravana.core.XSDNCore;

import java.util.HashMap;
import java.util.Map;

/**
 * Representing the building block of the flows.
 */
public class Chunk extends XSDNCore implements java.io.Serializable {
    private static Logger logger = LogManager.getLogger(Chunk.class.getName());

    private double size;
    private boolean isWaitStrictlyOrdered; //true for strict. false for relaxed.
    private double waitTime;
    private String currentNode;
    private int id;

    private Map<String, Double> propertyValuesForTheChunk = new HashMap<>();
    private String[] designatedRoute;

    // For the following nodes.
    private String nextNode;

    private boolean isLeadingChunk;

    /*routing is ignited when the starting time is reached in the first chunk of the flow, or when the previous chunk
    has left the node.
    Case D: igniteTime = previous chunk's start time.
    Case G: igniteTime = previous chunk has left the node completely.*/
    private double igniteTime = 0;

    public double getStartTime() {
        return igniteTime + waitTime;
    }

    /**
     * Routing the chunk, one node to the other, one link at a time.
     */
    public double startRouting() {
        logger.debug("Starts routing");
        return getStartTime();
    }

    public double getTimeWhenReachingDestination() {
        double timeReachingDestination = getStartTime() + getTotalTransmissionTime();
        if (logger.isInfoEnabled()) {
            logger.info("Chunk " + id + " : Time when reaching destination: " + timeReachingDestination);
        }
        return timeReachingDestination;
    }

    public double getTotalTransmissionTime() {
        String tempCurrentNode, tempNextNode;
        double currentSpeed;
        double totalTime = 0;
        for (int currentNodeI = 0; currentNodeI < designatedRoute.length - 1; currentNodeI++) {
            tempCurrentNode = designatedRoute[currentNodeI];
            tempNextNode = designatedRoute[(currentNodeI+1)];
            currentSpeed =  xSDNNodes.get(tempCurrentNode).getProperty(tempNextNode, "speed");
            if (logger.isTraceEnabled()) {
                logger.trace(tempCurrentNode + "=>" + tempNextNode + ": " + currentSpeed);
            }
            totalTime += getTransmissionTimeThroughEachLink(currentSpeed);
        }

        return totalTime;
    }

    /**
     * How long does it take to transmit this chunk. Mostly to be used for Case G.
     * @param speed, speed in units for the link (a connection between two nodes), say, bits/seconds
     * @return time taken to transmit from one node to the next node.
     */
    public double getTransmissionTimeThroughEachLink(double speed) {
        return size/speed;
    }

    public double getProperty(String property) {
        return propertyValuesForTheChunk.get(property);
    }

    /**
     * For properties such as Energy Consumption, Monetary cost, where values are for a unit of size.
     * @param property, name of the property
     */
    public void addMultiplicationProperty(String property) {
        double currentUnitValue = XSDNCore.getProperty(currentNode, nextNode, property);
        if (!propertyValuesForTheChunk.containsKey(property)) {
            propertyValuesForTheChunk.put(property, currentUnitValue);
        } else {
            double earlyTotalValue = propertyValuesForTheChunk.get(property);
            double currentValueAddition = currentUnitValue * size;
            propertyValuesForTheChunk.put(property, (earlyTotalValue+currentValueAddition));
        }
    }

    /**
     * For properties where values are for the link, regardless of the size of the transmission.
     * Seems no real example or use case yet for this type of property though.
     * @param property, name of the property
     */
    public void addSingularProperty(String property) {
        double currentUnitValue = XSDNCore.getProperty(currentNode, nextNode, property);
        if (!propertyValuesForTheChunk.containsKey(property)) {
            propertyValuesForTheChunk.put(property, currentUnitValue);
        } else {
            double earlyTotalValue = propertyValuesForTheChunk.get(property);
            propertyValuesForTheChunk.put(property, (earlyTotalValue+currentUnitValue));
        }
    }

    public Chunk(int id, double size, boolean isWaitStrictlyOrdered, double waitTime) {
        this.id = id;
        this.size = size;
        this.isWaitStrictlyOrdered = isWaitStrictlyOrdered;
        if (logger.isTraceEnabled()) {
            logger.trace("Setting the wait time: " + waitTime);
        }
        this.waitTime = waitTime;
    }

    public String getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(String currentNode) {
        this.currentNode = currentNode;
    }

    public double getSize() {
        return size;
    }

    public boolean isWaitStrictlyOrdered() {
        return isWaitStrictlyOrdered;
    }

    public double getWaitTime() {
        return waitTime;
    }

    public boolean isLeadingChunk() {
        return isLeadingChunk;
    }

    public void setLeadingChunk(boolean isLeadingNode) {
        this.isLeadingChunk = isLeadingNode;
    }

    public String getNextNode() {
        return nextNode;
    }

    public void setNextNode(String nextNode) {
        this.nextNode = nextNode;
    }

    public int getId() {
        return id;
    }

    public String[] getDesignatedRoute() {
        return designatedRoute;
    }

    public void setDesignatedRoute(String[] designatedRoute) {
        this.designatedRoute = designatedRoute;
    }

    public double getIgniteTime() {
        return igniteTime;
    }

    public void setIgniteTime(double igniteTime) {
        this.igniteTime = igniteTime;
    }
}
