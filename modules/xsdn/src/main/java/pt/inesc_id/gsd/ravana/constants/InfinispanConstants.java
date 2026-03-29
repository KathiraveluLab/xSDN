/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.constants;

import java.io.File;

/**
 * Constants for Infinispan integration
 */
public class InfinispanConstants {
    public static final String INFINISPAN_CONFIG_FILE = XSDNConstants.CONF_FOLDER + File.separator + "infinispan.xml";
    public static String TRANSACTIONAL_CACHE = "transactional";
}
