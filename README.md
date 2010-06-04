hData Java Parser
=================

This repository provides a set of tools for working with [hData](http://www.projecthdata.org) using Java. This includes [JAXB](https://jaxb.dev.java.net/) classes for working with hData related XML and a web application that implements the hData RESTful Network Transport API.

Building
--------

This project is built with [Apache Ant](http://ant.apache.org/) and dependencies are managed with [Apache Ivy](http://ant.apache.org/ivy/). Running

    > ant get-deps

Ivy will examine the ivy.xml file for dependencies and download them. To generate the JAXB classes run:

    > ant generate-jaxb-code

This will download the latest schemas from the [hData Git Repository](http://github.com/projecthdata/hData) and generate the JAXB classes. To create the web application run:

    > ant war

This will produce a war file that can be deployed to a java application server

License
-------

Copyright 2010 The MITRE Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
    