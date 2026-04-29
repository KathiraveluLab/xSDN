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
    public static final String TRANSACTIONAL_CACHE = "transactional";
    public static final String STATISTICS_CACHE = "statistics";
    public static final String NODES_CACHE = "nodes";
    public static final String FLOWS_CACHE = "flows";
    public static final String POLICIES_CACHE = "policies";
    public static final String ROUTES_CACHE = "routes";
    public static final String BEST_ROUTES_CACHE = "best_routes";
}
