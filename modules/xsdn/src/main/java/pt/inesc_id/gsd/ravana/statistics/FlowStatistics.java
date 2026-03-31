package pt.inesc_id.gsd.ravana.statistics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Captures the output empirical metrics of a completed network flow
 * to inform future adaptive routings.
 */
public class FlowStatistics implements Serializable {
    private String flowId;
    private String origin;
    private String destination;
    private double timeTakenEnroute;

    private Map<String, Double> properties = new HashMap<>();

    public FlowStatistics(String flowId, String origin, String destination, double timeTakenEnroute) {
        this.flowId = flowId;
        this.origin = origin;
        this.destination = destination;
        this.timeTakenEnroute = timeTakenEnroute;
    }

    public String getFlowId() {
        return flowId;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public double getTimeTakenEnroute() {
        return timeTakenEnroute;
    }

    public void addProperty(String propertyName, double value) {
        properties.put(propertyName, value);
    }

    public double getProperty(String propertyName) {
        return properties.getOrDefault(propertyName, 0.0);
    }

    public Map<String, Double> getProperties() {
        return properties;
    }
}
