GridDB Kafka Connector

# Overview

The Griddb Kafka Connector is a Kafka Connector for loading data to and from GridDB database with [Apache Kafka](https://kafka.apache.org/).

# Operating environment

Building of the library and execution of the sample programs have been checked in the following environment.

    OS: Ubuntu 24.04(x64)
    Java: 17
    Maven: 3.9.5
    Kafka: 2.13-3.7.1
    GridDB server: V5.6 CE, Ubuntu 22.04(x64)

# Build

```console
$ cd GRIDDB_KAFKA_CONNECTOR_FOLDER
$ mvn clean install
```

# Run

After build, put jar file GRIDDB_KAFKA_CONNECTOR_FOLDER/target/griddb-kafka-connector-X.X.X.jar to KAFKA_FOLDER/libs.
Start Kafka server.

Note: X.X.X is the software version.

There are 2 sample config files in config folder to run Sink and Source connector.

## Run the GridDB sink/source connector with the sample config file

Please refer to ["Using Apache Kafka with GridDB database"](docs/GridDB-kafka-sink-connect-and-source-connect-guide.md).

# Function

(available)
- STRING, BOOL, BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, TIMESTAMP, BLOB type for GridDB

(not available)
- GEOMETRY, ARRAY type for GridDB

# Community

  * Issues  
    Use the GitHub issue function if you have any requests, questions, or bug reports. 
  * PullRequest  
    Use the GitHub pull request function if you want to contribute code.
    You'll need to agree GridDB Contributor License Agreement(CLA_rev1.1.pdf).
    By using the GitHub pull request function, you shall be deemed to have agreed to GridDB Contributor License Agreement.

# License
  
  This GridDB Kafka Connector source is licensed under the Apache License, version 2.0.
  
# Trademarks
  
  Apache Kafka, Kafka are either registered trademarks or trademarks of the Apache Software Foundation in the United States and/or other countries.
