/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.flow.emulator;

import pt.inesc_id.gsd.ravana.flow.Flow;

import java.util.UUID;

/**
 * Emulation of a flow.
 */
public class EmulatedFlow extends Flow {
    private EmulatedPacket[] packets;

    public EmulatedFlow(double startTime, String origin, String destination) {
        super(startTime, origin, destination);
    }


    public EmulatedFlow(String origin, String destination, int[] packetSizes) {
        this.origin = origin;
        this.destination = destination;
        packets = EmulatedPacketImpl.createArrayOfPackets(packetSizes);
    }

    public EmulatedFlow(String origin, String destination, int flowSize) {
        this.origin = origin;
        this.destination = destination;
        packets = EmulatedPacketImpl.createArrayOfPackets(flowSize);
    }

    public EmulatedFlow(String origin, String destination) {
        this(origin, destination, 1);
    }

    public EmulatedPacket[] getPackets() {
        return packets;
    }
}
