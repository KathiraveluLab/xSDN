/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.algorithms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.inesc_id.gsd.ravana.core.XSDNCore;
import pt.inesc_id.gsd.ravana.network.XSDNNode;
import pt.inesc_id.gsd.ravana.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Random initialRoute, with the possibility of returning to the previous node with the initialRoute removed.
 */
public class ReducedReturnsRandomWalk extends RandomWalk implements Route {
    private static Logger logger = LogManager.getLogger(ReducedReturnsRandomWalk.class.getName());

    /**
     * Gets the ID of a next node for any given node and previous node, randomly.
     *
     * @param nodeId,     the id of the current node.
     * @param previousId, the id of the previously traversed node.
     * @return the next node.
     */
    public static String getNextNode(String nodeId, String previousId) {
        XSDNNode xsdnNode = xSDNNodes.get(nodeId);
        Set<String> nodes = xsdnNode.returnAllNextNodes();

        int id = RandomUtil.randomLessThanMax(nodes.size() - 1);

        Object[] orderedNodes = nodes.toArray();

        if (((String) orderedNodes[id]).equalsIgnoreCase(previousId)) {
            if (orderedNodes.length > 1) {
                if (id > 0) {
                    id--;
                } else if (id == 0) {
                    id++;
                }
            }
        }
        return (String) orderedNodes[id];
    }

    /**
     * Random Walk the flow from the origin to destination, returns reduced
     *
     * @param flowId, the flow id.
     */
    public static void route(String flowId) {
        String origin = XSDNCore.getOrigin(flowId);
        String destination = XSDNCore.getDestination(flowId);

        String current = origin;
        String previous = current;
        String previousPrevious;
        List<String> nodesTraversed = new ArrayList<>();
        nodesTraversed.add(current);

        while (!current.equalsIgnoreCase(destination)) {
            previousPrevious = previous;
            previous = current;
            current = getNextNode(current, previousPrevious);
            nodesTraversed.add(current);

        }
        printRoute("[ReducedReturnsRW]", origin, destination, nodesTraversed);
    }
}
