package pt.inesc_id.gsd.ravana.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for custom {@link RoutingAlgorithm} implementations.
 *
 * <p>Researchers register their algorithms here before simulation starts.
 * {@code XSDNEngine.executeSimulations} checks this registry first before
 * falling back to the built-in {@code RandomRoute} and {@code AdaptiveRoute}.
 *
 * <p>Thread-safe: uses a {@link ConcurrentHashMap} to allow safe concurrent reads.
 *
 * <p>Example:
 * <pre>
 *   RoutingAlgorithmRegistry.register(new MyShortestPath());
 *   XSDNEngine.executeSimulations("MyShortestPath");
 * </pre>
 */
public class RoutingAlgorithmRegistry {
    private static final Logger logger = LogManager.getLogger(RoutingAlgorithmRegistry.class);

    private static final Map<String, RoutingAlgorithm> registry = new ConcurrentHashMap<>();

    private RoutingAlgorithmRegistry() {
        // Utility class
    }

    /**
     * Registers a custom routing algorithm.
     * Registration is case-insensitive: "myAlgo", "MYALGO", and "MyAlgo" map to the same slot.
     *
     * @param algorithm the algorithm implementation to register
     */
    public static void register(RoutingAlgorithm algorithm) {
        String name = algorithm.getName().toLowerCase();
        registry.put(name, algorithm);
        logger.info("Registered custom routing algorithm: {}", algorithm.getName());
        System.out.println("[RoutingAlgorithmRegistry] Registered: " + algorithm.getName());
    }

    /**
     * Looks up a registered algorithm by name (case-insensitive).
     *
     * @param name the algorithm name
     * @return the registered {@link RoutingAlgorithm}, or {@code null} if not found
     */
    public static RoutingAlgorithm get(String name) {
        return name != null ? registry.get(name.toLowerCase()) : null;
    }

    /**
     * Returns whether an algorithm with the given name is registered.
     *
     * @param name the algorithm name
     * @return {@code true} if registered
     */
    public static boolean contains(String name) {
        return name != null && registry.containsKey(name.toLowerCase());
    }

    /**
     * Removes all registered custom algorithms. Useful for test teardown.
     */
    public static void clear() {
        registry.clear();
    }

    /**
     * Returns the number of registered algorithms.
     */
    public static int size() {
        return registry.size();
    }
}
