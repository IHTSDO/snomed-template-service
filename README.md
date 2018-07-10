Snomed Template Service
====================================

## Overview
Snomed Template Service REST API offers the following back-end services:

1. Maintaining and authoring Snomed Templates
2. Searching concepts by template logically, lexically or both
3. Generating new concepts that conformed to a given template
4. Transforming concepts from a source template to a destination template

## Quick Start
Use Maven to build the executable jar and run:
```bash
mvn clean package
java -Xmx3g -jar target/snomed-template-service*.jar
```
Access the service **API documentation** at [http://localhost:8086/template-service](http://localhost:8086/template-service).
The default username and password is _user_/_password_.

## Setup

Snomed Template Service application needs to connect to the TermServer for querying SNOMED CT concepts. The {{**terminologyserver.urll**}} parameter is used to configure this.

To improve performance the templates are cached. **POST /templates/reload** will clear all caches and should be used when new templates are created/
added to **snomed-templates** store. The snomed-templates store is configured via {{**templateStorePath**}} parameter.

{{**transformation.job.storage.local.path**}} is used to configure the root path where template transformation status and result files are stored. The default value for local storage is store/transformations

{{**transformation.job.storage.useCloud**}} is used to indicate whether to use cloud or local storage. When the value is set to false, status and results will be written to the local storage configured above.


### Configuration options
The default configuration of this Spring Boot application can be found in [application.properties](blob/master/src/main/resources/application.properties). The defaults can be overridden using command line arguments, for example set a different HTTP port:
```bash
java -Xmx3g -jar target/snomed-template-service*.jar --server.port=8081
```
For other options see [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html).

The default username and password (user:password) can be changed using the _security.user.name_ and _security.user.password_ properties.

## Building for Debian/Ubuntu Linux
A Debian package can be created using the 'deb' maven profile. 
```bash
mvn clean package -Pdeb
```