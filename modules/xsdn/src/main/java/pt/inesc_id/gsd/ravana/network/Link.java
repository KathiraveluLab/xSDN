/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.network;

import java.util.Map;

/**
 * The class representing Link
 */
public class Link implements java.io.Serializable {
    private String currentNode;
    private String nextNode;
    private Double propertyValue;
    private Map<String, Double> propertiesMap;

    public Link(String currentNode, String nextNode, Double propertyValue) {
        this.currentNode = currentNode;
        this.nextNode = nextNode;
        this.propertyValue = propertyValue;
    }

    public Link(String currentNode, String nextNode, Map<String, Double> propertiesMap) {
        this.currentNode = currentNode;
        this.nextNode = nextNode;
        this.propertiesMap = propertiesMap;
    }

    public String getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(String currentNode) {
        this.currentNode = currentNode;
    }

    public String getNextNode() {
        return nextNode;
    }

    public void setNextNode(String nextNode) {
        this.nextNode = nextNode;
    }

    public Double getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(Double propertyValue) {
        this.propertyValue = propertyValue;
    }

    public Map<String, Double> getPropertiesMap() {
        return propertiesMap;
    }

    public void setPropertiesMap(Map<String, Double> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }
}
