/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.flow;

/**
 * Abstract Flow representation
 */
public abstract class Flow implements java.io.Serializable {
    protected double startTime;
    protected String origin;
    protected String destination;

    public Flow(double startTime, String origin, String destination) {
        this.startTime = startTime;
        this.origin = origin;
        this.destination = destination;
    }

    public Flow() {}

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public double getStartTime() {
        return startTime;
    }
}
