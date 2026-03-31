package pt.inesc_id.gsd.ravana.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pt.inesc_id.gsd.ravana.core.XSDNCore;
import pt.inesc_id.gsd.ravana.core.XSDNEngine;
import pt.inesc_id.gsd.ravana.flow.XSDNFlow;
import pt.inesc_id.gsd.ravana.infinispan.InfCore;
import pt.inesc_id.gsd.ravana.main.Initiator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the northbound RoutingAlgorithm SPI:
 * - Custom algorithms can be registered and invoked by XSDNEngine.
 * - The registry takes precedence over built-in algorithms.
 */
public class RoutingAlgorithmRegistryTest {

    @Before
    public void setUp() {
        XSDNCore.getXSDNFlows().clear();
        XSDNCore.getXSDNNodes().clear();
        XSDNCore.getxSDNPolicies().clear();
        RoutingAlgorithmRegistry.clear();
    }

    @Test
    public void testCustomAlgorithmIsInvokedByEngine() {
        // 1. Implement a minimal custom routing algorithm
        AtomicInteger callCount = new AtomicInteger(0);
        List<String> routedFlows = new ArrayList<>();

        RoutingAlgorithm customAlgo = new RoutingAlgorithm() {
            @Override
            public String getName() {
                return "NullRouteTest";
            }

            @Override
            public void route(String flowId) {
                callCount.incrementAndGet();
                routedFlows.add(flowId);
                // Mark completed so the simulation loop terminates
                XSDNFlow flow = XSDNCore.getXSDNFlows().get(flowId);
                if (flow != null) {
                    flow.setCompleted(true);
                }
            }
        };

        // 2. Register and verify it is in the registry
        RoutingAlgorithmRegistry.register(customAlgo);
        assertTrue("Algorithm should be registered", RoutingAlgorithmRegistry.contains("NullRouteTest"));
        assertEquals(1, RoutingAlgorithmRegistry.size());

        // 3. Load the network and flows
        Initiator.initiateRouteList(1);
        XSDNEngine.initFlows();

        Map<String, XSDNFlow> flows = XSDNCore.getXSDNFlows();
        assertFalse("Must have flows to route", flows.isEmpty());

        // Count flows that will actually use our custom algorithm
        // (flows with a per-flow algorithm= override in flows.xml bypass the global algorithm)
        long expectedCalls = flows.values().stream()
                .filter(f -> f.getAlgorithm() == null)
                .count();

        // 4. Run the simulation — the registry should dispatch to our custom algorithm for eligible flows
        XSDNEngine.executeSimulations("NullRouteTest");

        // 5. Verify that all flows without a per-flow override were routed by our custom algorithm
        assertTrue("Custom algorithm should have been called for flows without per-flow override",
                callCount.get() >= expectedCalls);
        for (Map.Entry<String, XSDNFlow> entry : flows.entrySet()) {
            if (entry.getValue().getAlgorithm() == null) {
                assertTrue("Custom algo should have routed flow " + entry.getKey(),
                        routedFlows.contains(entry.getKey()));
            }
        }
    }

    @Test
    public void testRegistryClearRemovesAlgorithms() {
        RoutingAlgorithmRegistry.register(new RoutingAlgorithm() {
            @Override public String getName() { return "TestAlgo"; }
            @Override public void route(String flowId) {}
        });
        assertEquals(1, RoutingAlgorithmRegistry.size());
        RoutingAlgorithmRegistry.clear();
        assertEquals(0, RoutingAlgorithmRegistry.size());
        assertFalse(RoutingAlgorithmRegistry.contains("TestAlgo"));
    }

    @After
    public void tearDown() {
        RoutingAlgorithmRegistry.clear();
        InfCore.stop();
    }
}
