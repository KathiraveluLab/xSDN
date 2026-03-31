package pt.inesc_id.gsd.ravana.api;

/**
 * Northbound SPI for custom routing algorithms in xSDN.
 *
 * <p>Researchers can implement this interface to define a custom pathfinding
 * strategy and register it with {@link RoutingAlgorithmRegistry} without
 * modifying {@code XSDNEngine} source code.
 *
 * <p>Example usage:
 * <pre>
 *   public class MyShortestPath implements RoutingAlgorithm {
 *       {@literal @}Override
 *       public String getName() { return "MyShortestPath"; }
 *
 *       {@literal @}Override
 *       public void route(String flowId) {
 *           // Custom routing logic using XSDNCore, KnowledgeBase, etc.
 *       }
 *   }
 *
 *   // Register before calling executeSimulations:
 *   RoutingAlgorithmRegistry.register(new MyShortestPath());
 *   XSDNEngine.executeSimulations("MyShortestPath");
 * </pre>
 */
public interface RoutingAlgorithm {

    /**
     * Returns the unique name of this algorithm, used as the routing algorithm identifier
     * in calls to {@code XSDNEngine.executeSimulations(name)} and the {@code algorithm=}
     * attribute in {@code flows.xml}.
     *
     * @return algorithm name (case-insensitive match is used at dispatch time)
     */
    String getName();

    /**
     * Routes the specified flow to its destination using this algorithm's logic.
     *
     * <p>Implementations may read topology from {@code XSDNCore.getXSDNNodes()},
     * historical data from {@code KnowledgeBase.getBestHistoricalRoute()}, and
     * must mark the flow as completed by calling
     * {@code XSDNCore.getXSDNFlows().get(flowId).setCompleted(true)}.
     *
     * @param flowId the ID of the flow to route
     */
    void route(String flowId);
}
