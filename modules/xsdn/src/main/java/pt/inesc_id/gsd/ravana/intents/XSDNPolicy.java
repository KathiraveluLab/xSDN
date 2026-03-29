/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.intents;

import java.util.HashMap;
import java.util.Map;

/**
 * Policy for XSDN
 */
public class XSDNPolicy {
    private Map<String, String> policyMap = new HashMap<>();

    public void addPolicy(String key, String value) {
        policyMap.put(key,value);
    }

    public Map<String, String> getPolicyMap() {
        return policyMap;
    }


}
