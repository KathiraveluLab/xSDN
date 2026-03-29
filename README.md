# Project Overview

Welcome to the wiki of SDNSim!

Please visit the website to learn more - https://sourceforge.net/p/sdnsim/


This is an umbrella SDN project containing,

* RĀVAṆA - A Resilient and Adaptive Virtualization Architecture for Network Flow Algorithms.
* xSDN - An Expressive Simulator for Dynamic Network Flows.
* ESCALATOR - An Integrated Process for Building Software-Defined Cloud Networks Through Incremental Simulation and Emulation.


## Building Using Apache Maven 3.x.x
To build using maven:
mvn clean install

skipping the tests:
$ mvn clean install -Dmaven.skip.test=true

To skip executing the tests:
$ mvn clean install -DskipTests


Built distribution inside distribution/target.
--------------------------------------------------
$ tar -xzvf distribution/target/xsdn-1.0-SNAPSHOT.tar.gz
$ mv xsdn-1.0-SNAPSHOT/jars/xsdn-1.0-SNAPSHOT.jar .


## Executing
Respective Executors are found in the package, "main".


## Logging
Make sure to include conf/log4j2-test.xml into your class path to be able to configure and view the logs. Default log level is [WARN].


## Dependencies
This project depends on the below major projects.

* Apache Log4j2
* Infinispan
* OpenDaylight Controller
