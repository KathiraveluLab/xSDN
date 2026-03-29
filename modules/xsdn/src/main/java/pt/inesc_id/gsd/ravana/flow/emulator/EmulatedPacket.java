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
 * Emulating an abstract Packet class, representing the packets being transferred.
 */
public abstract class EmulatedPacket {
    protected Long id;
    protected String message;


    public EmulatedPacket() {
        this(RandomUtil.generateRandomInteger());
    }

    public EmulatedPacket(int packetSize) {
        id = RandomUtil.generateRandomLong();
        message = RandomUtil.generateRandomString(packetSize);
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
