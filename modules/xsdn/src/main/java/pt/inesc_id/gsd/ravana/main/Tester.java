/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.main;

import pt.inesc_id.gsd.ravana.algorithms.CyclesTruncatedRandomWalk;
import pt.inesc_id.gsd.ravana.algorithms.RandomWalk;
import pt.inesc_id.gsd.ravana.algorithms.ReducedReturnsRandomWalk;

import java.util.Set;

/**
 * A few methods, that may be called by the executor.
 */
public class Tester {
    /**
     * A method to try all the routing algorithms.
     * @param flows all the flows.
     */
    public static void tryAllRouteAlgorithms(Set<String> flows) {
        for (String flow : flows) {
            RandomWalk.route(flow);
        }

        for (String flow : flows) {
            CyclesTruncatedRandomWalk.route(flow);
        }

        for (String flow : flows) {
            ReducedReturnsRandomWalk.route(flow);
        }
    }
}
