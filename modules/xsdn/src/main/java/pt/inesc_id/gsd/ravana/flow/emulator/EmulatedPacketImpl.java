/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.flow.emulator;

import pt.inesc_id.gsd.ravana.util.RandomUtil;

/**
 * A base Packet emulation
 */
public class EmulatedPacketImpl extends EmulatedPacket {

    public EmulatedPacketImpl(int packetSize) {
        super(packetSize);
    }

    public EmulatedPacketImpl() {
        super();
    }

    /**
     * Duplicates a given packet
     * @param packet, the given packet
     * @return the duplicate packet
     */
    public static EmulatedPacketImpl duplicatePacket(EmulatedPacketImpl packet) {
        EmulatedPacketImpl duplicatePacket = new EmulatedPacketImpl();
        duplicatePacket.id = RandomUtil.generateRandomLong();
        duplicatePacket.message = packet.message;
        return duplicatePacket;
    }

    /**
     * Creates an array of packets with given sizes.
     * @param packetSizes, the array of sizes of the packets to be created
     * @return the array of packets
     */
    public static EmulatedPacketImpl[] createArrayOfPackets(int[] packetSizes) {
        EmulatedPacketImpl[] packets = new EmulatedPacketImpl[packetSizes.length];
        for (int i = 0; i< packetSizes.length; i++) {
            packets[i] = new EmulatedPacketImpl(packetSizes[i]);
        }
        return packets;
    }

    /**
     * Creates an array of packets with random sizes.
     * @param noOfPackets, the number of packets to be in the array
     * @return the array of packets
     */
    public static EmulatedPacketImpl[] createArrayOfPackets(int noOfPackets) {
        EmulatedPacketImpl[] packets = new EmulatedPacketImpl[noOfPackets];
        for (int i = 0; i< noOfPackets; i++) {
            packets[i] = new EmulatedPacketImpl();
        }
        return packets;
    }
}
