SNOMED CT Transformation and Template Service
============================================

## Overview
SNOMED Transformation and Template Service is a REST API with the following functions:

- Authoring using [SNOMED Templates](https://github.com/IHTSDO/snomed-templates)
- Searching concepts by template logically, lexically or both
- Generating new concepts that conformed to a given template
- Transforming concepts from a source template to a destination template

## Quick Start
Use Maven to build the executable jar and run:
```bash
mvn clean package
java -Xmx3g -jar target/snomed-template-service*.jar
```
Access the service **API documentation** at [http://localhost:8086/template-service](http://localhost:8086/template-service).
The default username and password is _user_/_password_.

### Configuration
The default configuration of this Spring Boot application can be found in [application.properties](src/main/resources/application.properties). The defaults can be overridden by creating an override `application.properties`, file in the same directory as the jar, containing just the options you want to override. Alternatively options can be set using command line arguments, for example set a different HTTP port:
```bash
java -Xmx3g -jar target/snomed-template-service*.jar --server.port=8081
```
For other options see [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html).

The default username and password (user:password) can be changed using the `security.user.name` and `security.user.password` properties.

## Setup
The service needs to connect to the Snowstorm terminology server for querying SNOMED CT concepts. The `terminologyserver.url` parameter is used to configure this.

Clone the [snomed-templates](https://github.com/IHTSDO/snomed-templates) repository into the same directory as the template service. These templates will be loaded by the service to be used for concept authoring and transformations.
To improve performance the templates are cached. **POST /templates/reload** will clear all caches and should be used when new templates are created/
added to the **snomed-templates** store. The snomed-templates store is configured via `templateStorePath` parameter.

`transformation.job.storage.local.path` is used to configure the root path where template transformation status and result files are stored. The default value for local storage is store/transformations.

`transformation.job.storage.useCloud` is used to indicate whether to use cloud or local storage, this defaults to false/local.

## Building for Debian/Ubuntu Linux
A Debian package can be created using the 'deb' maven profile. 
```bash
mvn clean package -Pdeb
```
