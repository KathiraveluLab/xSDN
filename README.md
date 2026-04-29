# xSDN

An Expressive Simulator for Dynamic Network Flows.


## Setup and Building
To automatically build the project and extract the core JAR:
```bash
chmod +x setup.sh
./setup.sh
```

## Executing

Executors are found in the `main` package. Run with `mvn exec:java` using the following CLI system properties:

| Property | Values | Default | Description |
|----------|--------|---------|-------------|
| `-Dalgo` | `RandomRoute`, `AdaptiveRoute` | `RandomRoute` | Routing algorithm |
| `-Dconf` | path to a directory | `conf` | Configuration directory (XML files) |
| `-Dhealth` | `true`, `false` | `false` | Enable HealthMonitor telemetry output |

```bash
# Default run (RandomRoute, conf/, no health monitoring)
mvn exec:java -pl modules/xsdn -Dexec.mainClass="pt.inesc_id.gsd.ravana.main.XSDNExecutor"

# Adaptive routing with health monitoring
mvn exec:java -pl modules/xsdn -Dexec.mainClass="pt.inesc_id.gsd.ravana.main.XSDNExecutor" \
    -Dalgo=AdaptiveRoute -Dhealth=true

# Benchmark run on a generated caveman topology
mvn exec:java -pl modules/xsdn -Dexec.mainClass="pt.inesc_id.gsd.ravana.main.XSDNExecutor" \
    -Dalgo=AdaptiveRoute -Dconf=conf/benchmarking -Dhealth=true
```


## Logging
xSDN uses Log4j2 for logging. The configuration is located at `conf/log4j2-test.xml` and is automatically included in the classpath during builds. The default log level is set to `INFO`.


## Documentation

- **[Routing Engine Architecture](docs/routing_engine.md)** — How the Infinispan distributed cache connects to `XSDNFlow`, the adaptive routing feedback loop, SLA intent policies, and how to add custom algorithms.


## OpenDaylight (ODL) Integration

xSDN provides the core simulation and adaptive routing engine. For real-world emulation with OpenDaylight and Mininet, see **[SENDIM](https://github.com/KathiraveluLab/SENDIM)** — a framework built on top of xSDN that bridges the xSDN engine to the OpenDaylight northbound API, enabling fully emulated SDN environments.


## Future Work

- **Real-time Web Dashboard**: A lightweight HTML/JS dashboard that polls the `KnowledgeBase` to visualize live network topology and animate flow heatmaps, providing a richer experience than terminal-only output.
- **Full Topology Distribution**: Fully distributing `RouteInitiator.possibleRoutes` via Infinispan is partially satisfied — `KnowledgeBase` is already distributed. Full topology distribution is left as future work.


## Citing xSDN

If you use xSDN in your research, please cite the below paper:

* Kathiravelu, Pradeeban; Veiga, Luis, **An Expressive Simulator for Dynamic Network Flows,** Cloud Engineering (IC2E), 2015 IEEE International Conference on , vol., no., pp.311,316, 9-13 March 2015
doi: 10.1109/IC2E.2015.43 
