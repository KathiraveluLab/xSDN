# xSDN

An Expressive Simulator for Dynamic Network Flows.


## Building Using Apache Maven 3.x.x
To build using maven:
mvn clean install

skipping the tests:
```bash
mvn clean install -Dmaven.skip.test=true
```

To skip executing the tests:
```bash
mvn clean install -DskipTests
```

### Built distribution inside distribution/target.
```bash
tar -xzvf distribution/target/xsdn-1.0-SNAPSHOT.tar.gz

mv xsdn-1.0-SNAPSHOT/jars/xsdn-1.0-SNAPSHOT.jar .
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
Make sure to include conf/log4j2-test.xml into your class path to be able to configure and view the logs. Default log level is [WARN].


## Documentation

- **[Routing Engine Architecture](docs/routing_engine.md)** — How the Infinispan distributed cache connects to `XSDNFlow`, the adaptive routing feedback loop, SLA intent policies, and how to add custom algorithms.


## OpenDaylight (ODL) Integration

xSDN provides the core simulation and adaptive routing engine. For real-world emulation with OpenDaylight and Mininet, see **[SENDIM](https://github.com/KathiraveluLab/SENDIM)** — a framework built on top of xSDN that bridges the xSDN engine to the OpenDaylight northbound API, enabling fully emulated SDN environments.


## Future Work

- **Real-time Web Dashboard**: A lightweight HTML/JS dashboard that polls the `KnowledgeBase` to visualize live network topology and animate flow heatmaps, providing a richer experience than terminal-only output.


## Citing xSDN

If you use xSDN in your research, please cite the below paper:

* Kathiravelu, Pradeeban; Veiga, Luis, **An Expressive Simulator for Dynamic Network Flows,** Cloud Engineering (IC2E), 2015 IEEE International Conference on , vol., no., pp.311,316, 9-13 March 2015
doi: 10.1109/IC2E.2015.43 
