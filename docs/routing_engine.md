# xSDN Routing Engine Architecture

> **For researchers inheriting this codebase:** This document explains how the core simulation pipeline works, how the Infinispan distributed cache integrates with the routing logic, and how the system is extended.

---

## Overview

xSDN simulates dynamic network flows using an **adaptive, policy-driven routing engine**. The pipeline moves from XML configuration parsing → topology construction → route initialization → simulation → knowledge capture → adaptive feedback.

```
network.xml ──► NetworkBuilder ──► XSDNCore (Nodes/Links)
flows.xml   ──► FlowBuilder   ──► XSDNCore (XSDNFlows + Chunks)
policy.xml  ──► PolicyBuilder ──► XSDNCore (XSDNPolicies / SLA profiles)
initFlows.xml ► RouteInitiator ► possible routes (random walks)
                                        │
                                        ▼
                           XSDNEngine.executeSimulations()
                                        │
                           ┌────────────┴────────────┐
                           │       AdaptiveRoute       │
                           │  (or RandomRoute, etc.)   │
                           └────────────┬────────────┘
                                        │
                           Per-flow, per-chunk routing
                                        │
                                        ▼
                              XSDNFlow.startRouting()
                           (Chunk transmission times,
                            energy, cost, speed calc)
                                        │
                                        ▼
                             FlowStatistics (metrics)
                                        │
                                        ▼
                    KnowledgeBase.addFlowStatistics()
                    ┌───────────────────────────────┐
                    │  flowStatisticsMap             │ ← all flow metrics
                    │  bestRouteStatisticsMap        │ ← O(1) index by
                    │  key: origin + dest + property │   SLA objective
                    └───────────────────────────────┘
                                        │
                              (next flow uses history)
                                        │
                                        ▼
                    AdaptiveRoute checks best historical
                    route for the flow's SLA profile
```

---

## Key Components

### 1. `XSDNCore`
The central singleton that holds all runtime state:
- `xSDNNodes` — map of network nodes and their link properties (speed, energy, cost)
- `xSDNFlows` — map of active flows, each containing ordered `Chunk` objects
- `xSDNPolicies` — map of SLA intent policies (`minimize energy`, `maximize speed`, etc.)

### 2. `XSDNFlow` & `Chunk`
- An **`XSDNFlow`** represents a network data transfer with a defined `origin`, `destination`, and `profile` (SLA intent).
- It is subdivided into **`Chunk`** objects, each independently routed over the network graph.
- Each `Chunk` records SLA properties (`energy`, `cost`) per link traversal via `addMultiplicationProperty()`.

### 3. `RouteInitiator` & `CyclesTruncatedRandomWalk`
Before simulation, `RouteInitiator.initializePossibleRoutesList()` pre-calculates candidate paths for each flow using **random walks** with cycle truncation.
- A `maxHops` limit prevents infinite loops in high-cluster topologies (e.g., caveman graphs).

### 4. `AdaptiveRoute`
The primary routing algorithm that applies the **knowledge feedback loop**:
1. **History Check**: Queries `KnowledgeBase` for the best historical route matching the flow's SLA profile (O(1) via composite key).
2. **Routing**: If history exists, uses the best known route. Otherwise falls back to random walk.
3. **Feedback**: After routing, computes `FlowStatistics` including:
   - `timeTakenEnroute`, `energy`, `cost`, `speed`
   - Statistical distributions: `time_variance`, `time_stddev`, `time_p95`, `speed_variance`, `speed_stddev`, `speed_p95`
4. **Index Update**: Stores results in `KnowledgeBase`, updating best-route indices for all SLA properties.

### 5. `KnowledgeBase`
The shared knowledge store powering adaptive routing decisions.

| Map | Key | Value |
|-----|-----|-------|
| `flowStatisticsMap` | `flowId` | Full `FlowStatistics` record |
| `bestRouteStatisticsMap` | `"origin dest property"` | Best `FlowStatistics` for that SLA goal |

**Infinispan Integration**: `KnowledgeBase.initInfinispan()` replaces the local `HashMap` with a distributed `Cache` backed by Infinispan (via `InfCore`). This enables multi-node cluster deployments where routing history is shared across JVM instances.

> ⚠️ If Infinispan fails to initialize (e.g., missing config or version mismatch), `KnowledgeBase` falls back silently to a local `HashMap`. The simulation continues correctly in standalone mode.

### 6. `HealthMonitor`
A background daemon thread that samples system telemetry (CPU load, memory usage, system load average) during simulation. Activated via `Initiator.setIsHealthMonitoringEnabled(true)`.

---

## Infinispan ↔ XSDNFlow Integration

```
InfCore (singleton)
  └── DefaultCacheManager (infinispan.xml)
        ├── statisticsCache  ──► KnowledgeBase.flowStatisticsMap
        ├── bestRoutesCache  ──► KnowledgeBase.bestRouteStatisticsMap
        ├── nodesCache       ──► (optional: distributed XSDNNodes)
        ├── flowsCache       ──► (optional: distributed XSDNFlows)
        └── routesCache      ──► (optional: distributed possible routes)
```

When `KnowledgeBase.initInfinispan()` is called:
- `InfCore.getInfiniCore()` creates the `DefaultCacheManager` from `conf/infinispan.xml`
- The local `HashMap` references in `KnowledgeBase` are **replaced** by live Infinispan `Cache` references
- All subsequent `addFlowStatistics()` calls write directly into the distributed cache
- Any other JVM node in the cluster with the same Infinispan configuration will see the same routing history

To cleanly shut down Infinispan (e.g., in tests), call `InfCore.stop()`.

---

## SLA Intent Routing

Each flow can have a `profile` attribute set in `flows.xml`:

```xml
<flow id="flow1" origin="A" destination="D" profile="energy">
```

Available profiles (defined in `policy.xml`):
| Profile | Goal | Property |
|---------|------|----------|
| `time` (default) | minimize | `timeTakenEnroute` |
| `energy` | minimize | `energy` |
| `cost` | minimize | `cost` |
| `bandwidth` | maximize | `speed` |

`AdaptiveRoute` selects the best historical route for a flow using its profile's `(property, isMinimize)` pair.

---

## Extending with Custom Algorithms

To add a custom routing algorithm:

1. Create a class in `pt.inesc_id.gsd.ravana.algorithms` with a static `route(String flowId)` method.
2. Register it in `XSDNEngine.executeSimulations()`:
   ```java
   } else if (routingAlgorithm.equalsIgnoreCase("MyCustomRoute")) {
       MyCustomRoute.route(flowId);
   }
   ```
3. Pass `-Dalgo=MyCustomRoute` (once CLI support is added) or call `XSDNEngine.executeSimulations("MyCustomRoute")` directly.

---

## Running the Simulator

```bash
# Build
mvn clean install -DskipTests

# Run with default conf/ XML files
mvn exec:java -pl modules/xsdn -Dexec.mainClass="pt.inesc_id.gsd.ravana.main.XSDNExecutor"

# Run tests
mvn clean test -pl modules/xsdn
```

### Benchmarking with Generated Topologies

```bash
# Generate Random, Small-World, and Caveman graph topologies
python3 scripts/graph_generator.py

# Generated files are placed in conf/benchmarking/
# Copy the desired topology into conf/ and re-run the executor
cp conf/benchmarking/network_caveman.xml conf/network.xml
cp conf/benchmarking/flows_caveman.xml conf/flows.xml
cp conf/benchmarking/init_flows_caveman.xml conf/initFlows.xml
mvn exec:java -pl modules/xsdn -Dexec.mainClass="pt.inesc_id.gsd.ravana.main.XSDNExecutor"
```

---

## Related Projects

- **[SENDIM](https://github.com/KathiraveluLab/SENDIM)** — Built on xSDN, bridges the routing engine to the OpenDaylight northbound API for real-world SDN emulation with Mininet.
