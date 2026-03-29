/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.flow.emulator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Emulates a basic flow
 */
public class FlowEmulator {
    private static Map<Long, EmulatedFlow> flowsMap = new HashMap<>();

    /**
     * Generates a flow with a single packet, between the origin and destination
     * @param origin the starting hop
     * @param destination the ending hop
     */
    public static void generateFlow(String origin, String destination) {
        Long flowId = UUID.randomUUID().getLeastSignificantBits();
        EmulatedFlow flow = new EmulatedFlow(origin, destination);
        flowsMap.put(flowId, flow);
    }

    /**
     * Generates a flow with packets of random sizes, between the origin and destination
     * @param origin, the starting hop
     * @param destination, the ending hop
     * @param flowSize, the number of packets in the flow.
     */
    public static void generateFlow(String origin, String destination, int flowSize) {
        Long flowId = UUID.randomUUID().getLeastSignificantBits();
        EmulatedFlow flow = new EmulatedFlow(origin, destination, flowSize);
        flowsMap.put(flowId, flow);
    }

    /**
     * Generates a flow with packets of given sizes, between the origin and the destination
     * @param origin, the starting hop
     * @param destination, the ending hop.
     * @param packetSizes, the sizes of the packets in the flow.
     */
    public static void generateFlow(String origin, String destination, int[] packetSizes) {
        Long flowId = UUID.randomUUID().getLeastSignificantBits();
        EmulatedFlow flow = new EmulatedFlow(origin, destination, packetSizes);
        flowsMap.put(flowId, flow);
    }

    /**
     * Returns the map of flows
     * @return the flow map
     */
    public static Map<Long, EmulatedFlow> getFlowsMap() {
        return flowsMap;
    }
}
