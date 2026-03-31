/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents the characteristics of a XSDN node.
 */
public class XSDNNode implements java.io.Serializable {
    private static Logger logger = LogManager.getLogger(XSDNNode.class.getName());

    private Map<String, Map<String, Double>> primaryNextMap = new HashMap<>();

    /**
     * Depicts the link to each next node
     * @param key: id of the next node
     * @param propertyMap, properties of the link to the next node.
     */
    public void addNextNode(String key, Map<String, Double> propertyMap) {
        primaryNextMap.put(key,propertyMap);
    }

    /**
     * Returns the map of next nodes of the current node object
     * @return the map of next nodes.
     */
    public Map<String, Map<String, Double>> getPrimaryNextMap() {
        return primaryNextMap;
    }

    public Set<String> returnAllNextNodes() {
        return primaryNextMap.keySet();
    }

    /**
     * Returns the value of the given property for any link
     * @param neighbourNodeId, the id of the neighbor node
     * @param propertyName, name of the property
     * @return the value of the property
     */
    public Double getProperty(String neighbourNodeId, String propertyName) {
        return primaryNextMap.get(neighbourNodeId).get(propertyName);
    }

    /**
     * Returns the property map for any link
     * @param neighbourNodeId, the id of the neighbor node
     * @return the property map
     */
    public Map<String, Double> getAllProperties(String neighbourNodeId) {
        return primaryNextMap.get(neighbourNodeId);
    }
}
