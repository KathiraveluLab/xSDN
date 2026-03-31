package pt.inesc_id.gsd.ravana.statistics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pt.inesc_id.gsd.ravana.core.XSDNCore;
import pt.inesc_id.gsd.ravana.core.XSDNEngine;
import pt.inesc_id.gsd.ravana.flow.XSDNFlow;
import pt.inesc_id.gsd.ravana.infinispan.InfCore;
import pt.inesc_id.gsd.ravana.main.Initiator;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Validates the Infinispan cache injection and AdaptiveRoute engine logic.
 */
public class AdaptiveRouteTest {

    @Before
    public void setUp() {
        // Reset ALL global static state before each test run
        XSDNCore.getXSDNFlows().clear();
        XSDNCore.getXSDNNodes().clear();
        XSDNCore.getxSDNPolicies().clear();
        KnowledgeBase.getAllStatistics().clear();
    }

    @Test
    public void testAdaptiveRoutingWithInfinispan() {
        // 1. Set up network nodes, topology, policies and initial possible routes
        //    (Initiator.initiateRouteList calls XSDNEngine.initNetworkAndPolicies internally)
        Initiator.initiateRouteList(1);

        // 2. Parse and load flow definitions after the network is ready
        XSDNEngine.initFlows();

        Map<String, XSDNFlow> flows = XSDNCore.getXSDNFlows();
        assertFalse("Parsed flows collection should be non-empty", flows.isEmpty());

        // 3. Attempt Infinispan distributed cache injection (falls back to local map on failure)
        KnowledgeBase.initInfinispan();

        // 4. Run full simulation pipeline for all loaded flows
        XSDNEngine.executeSimulations("AdaptiveRoute");

        // 5. Assert all flows completed routing
        for (Map.Entry<String, XSDNFlow> entry : flows.entrySet()) {
            assertTrue("Flow " + entry.getKey() + " should have completed routing",
                    entry.getValue().isCompleted());
        }

        // 6. Assert routing metrics were recorded in the KnowledgeBase (local or distributed)
        Map<String, FlowStatistics> cache = KnowledgeBase.getAllStatistics();
        assertNotNull("KnowledgeBase statistics reference should not be null", cache);
        assertFalse("KnowledgeBase should have recorded flow statistics after simulation", cache.isEmpty());
    }

    @After
    public void tearDown() {
        // Shut down Infinispan cache manager so cluster threads don't block JVM exit
        InfCore.stop();
    }
}
