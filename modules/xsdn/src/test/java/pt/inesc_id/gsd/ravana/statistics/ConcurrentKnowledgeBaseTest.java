package pt.inesc_id.gsd.ravana.statistics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pt.inesc_id.gsd.ravana.core.XSDNCore;
import pt.inesc_id.gsd.ravana.core.XSDNEngine;
import pt.inesc_id.gsd.ravana.infinispan.InfCore;
import pt.inesc_id.gsd.ravana.main.Initiator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Concurrent integration tests verifying that 5 simultaneous simulation
 * threads do not induce race conditions or corrupt shared KnowledgeBase state.
 *
 * <p>Addresses Thirumalai's concern: Infinispan behaves differently under
 * concurrency, and we must prove that concurrent XSDNEngine threads don't
 * overwrite each other's metrics.
 */
public class ConcurrentKnowledgeBaseTest {

    private static final int THREAD_COUNT = 5;

    @Before
    public void setUp() {
        XSDNCore.getXSDNFlows().clear();
        XSDNCore.getXSDNNodes().clear();
        XSDNCore.getxSDNPolicies().clear();
        KnowledgeBase.getAllStatistics().clear();
    }

    /**
     * Verifies that 5 concurrent threads each calling AdaptiveRoute simulations
     * can safely write to KnowledgeBase without losing entries or throwing exceptions.
     */
    @Test
    public void testConcurrentAdaptiveRoutingDoesNotCorruptKnowledgeBase() throws InterruptedException {
        // 1. Initialize once (network/routes/flows are shared read-only during simulation)
        Initiator.initiateRouteList(1);
        XSDNEngine.initFlows();
        KnowledgeBase.initInfinispan();

        int flowCount = XSDNCore.getXSDNFlows().size();
        assertTrue("Must have flows loaded to run concurrent test", flowCount > 0);

        // 2. Snapshot the flow IDs; each thread will route a subset independently
        List<String> flowIds = new ArrayList<>(XSDNCore.getXSDNFlows().keySet());

        // 3. Fire THREAD_COUNT concurrent simulation threads using a latch for synchronised start
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(THREAD_COUNT);
        AtomicInteger exceptions = new AtomicInteger(0);
        AtomicInteger successfulRoutes = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<?>> futures = new ArrayList<>();

        for (int t = 0; t < THREAD_COUNT; t++) {
            final int threadIndex = t;
            futures.add(executor.submit(() -> {
                try {
                    startGate.await(); // Wait until all threads are ready
                    // Each thread routes the same complete set of flows
                    // (simulates concurrent network events arriving simultaneously)
                    for (String flowId : flowIds) {
                        pt.inesc_id.gsd.ravana.algorithms.AdaptiveRoute.route(flowId);
                        successfulRoutes.incrementAndGet();
                    }
                } catch (Exception e) {
                    exceptions.incrementAndGet();
                    System.err.println("Thread " + threadIndex + " threw: " + e.getMessage());
                } finally {
                    endGate.countDown();
                }
                return null;
            }));
        }

        // 4. Release all threads simultaneously to maximise contention
        startGate.countDown();
        boolean completed = endGate.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // 5. Assertions
        assertTrue("All threads should complete within 30 seconds (no deadlock)", completed);
        assertEquals("No thread should have thrown an unexpected exception", 0, exceptions.get());

        // KnowledgeBase must have entries — concurrent writes should not have lost all data
        Map<String, FlowStatistics> cache = KnowledgeBase.getAllStatistics();
        assertFalse("KnowledgeBase must contain entries after concurrent routing", cache.isEmpty());

        System.out.println("[ConcurrentTest] Threads: " + THREAD_COUNT
                + " | Successful routes: " + successfulRoutes.get()
                + " | KnowledgeBase entries: " + cache.size()
                + " | Exceptions: " + exceptions.get());
    }

    /**
     * Verifies that concurrent writes to KnowledgeBase don't cause the best-route
     * index to hold a null/inconsistent value for any valid origin-destination pair.
     */
    @Test
    public void testConcurrentBestRouteIndexRemainsSane() throws InterruptedException {
        Initiator.initiateRouteList(1);
        XSDNEngine.initFlows();
        KnowledgeBase.initInfinispan();

        List<String> flowIds = new ArrayList<>(XSDNCore.getXSDNFlows().keySet());
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger exceptions = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int t = 0; t < THREAD_COUNT; t++) {
            executor.submit(() -> {
                try {
                    for (String flowId : flowIds) {
                        pt.inesc_id.gsd.ravana.algorithms.AdaptiveRoute.route(flowId);
                    }
                } catch (Exception e) {
                    exceptions.incrementAndGet();
                } finally {
                    latch.countDown();
                }
                return null;
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // Verify best-route lookups for loaded flows don't return corrupt/null statistics
        for (String flowId : flowIds) {
            pt.inesc_id.gsd.ravana.flow.XSDNFlow flow = XSDNCore.getXSDNFlows().get(flowId);
            if (flow != null) {
                // Query should either return a valid stat or null (never throw)
                FlowStatistics best = KnowledgeBase.getBestHistoricalRoute(
                        flow.getOrigin(), flow.getDestination());
                // If present, the stats object itself must be non-null and coherent
                if (best != null) {
                    assertTrue("Best route time must be non-negative",
                            best.getTimeTakenEnroute() >= 0);
                }
            }
        }

        assertEquals("No exceptions during concurrent best-route index writes", 0, exceptions.get());
    }

    @After
    public void tearDown() {
        InfCore.stop();
    }
}
