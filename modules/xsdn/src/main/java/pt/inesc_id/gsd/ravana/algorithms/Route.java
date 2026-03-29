/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.algorithms;

/**
 * Interface for routing algorithms
 */
public interface Route {

    /**
     * Perform a routing of the flow, using the algorithm
     * @param flowId, id of the flow.
     */
    public static void route(String flowId) {
    }
}
