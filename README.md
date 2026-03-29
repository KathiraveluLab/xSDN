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
Respective Executors are found in the package, "main".


## Logging
Make sure to include conf/log4j2-test.xml into your class path to be able to configure and view the logs. Default log level is [WARN].


## Citing xSDN

If you use xSDN in your research, please cite the below paper:

* Kathiravelu, Pradeeban; Veiga, Luis, **An Expressive Simulator for Dynamic Network Flows,** Cloud Engineering (IC2E), 2015 IEEE International Conference on , vol., no., pp.311,316, 9-13 March 2015
doi: 10.1109/IC2E.2015.43 
