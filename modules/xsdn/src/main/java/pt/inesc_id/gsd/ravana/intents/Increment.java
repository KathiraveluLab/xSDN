/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.intents;

import pt.inesc_id.gsd.ravana.constants.RavanaConstants;

/**
 * Where the user intent is to increase something.
 */
public abstract class Increment implements Intent {
    private int priority = 1;
    private int intent = RavanaConstants.IntentQuotient.MAXIMIZE;

    private int currentPriority = priority * intent;


    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getCurrentPriority() {
        return currentPriority;
    }

    public void setCurrentPriority(int currentPriority) {
        this.currentPriority = currentPriority;
    }
}
