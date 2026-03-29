/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.inesc_id.gsd.ravana.util.XSDNUtil;

/**
 * A sample execution of XSDN.
 */
public class XSDNExecutor {
    private static Logger logger = LogManager.getLogger(XSDNExecutor.class.getName());

    public static void main(String argv[]) {
        long startTime = System.currentTimeMillis();

//        Initiator.infInitAndExecute(10);
//        Initiator.infExecute();

//        Initiator.setIsHealthMonitoringEnabled(true);
//        Initiator.setWaitTimeInMillis(1000);

//        Initiator.initiate(10);
//        Initiator.initiateRouteList(10);

//        Initiator.initiateNetworkAndPolicies();

        Initiator.simulate(10);

        XSDNUtil.logTotalExecTime(startTime);
    }

}
