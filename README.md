## Topl DAML Demos

This repository contains some projects that illustrate how to implement a DAML app integrated with the Topl blockchain. All applications use the [daml-dopl-lib](https://github.com/Topl/daml-bifrost-module).

The projects are:

- java-daml-lattice-app
- scala-daml-broker-app

### java-daml-lattice-app

This project is a Spring Boot application written in Java that implements a very simple asset tracking app backed by the Topl blockchain.

### scala-daml-broker-app

This project is a simple scala application that implements a generic broker. It systematically captures the requests to transfer polys, and assets and processes them.