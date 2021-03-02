SNOMED CT Transformation and Template Service
============================================

## Overview
SNOMED Transformation and Template Service is a REST API with the following functions:

- Authoring using [SNOMED Templates](https://github.com/IHTSDO/snomed-templates)
- Searching concepts by template logically, lexically or both
- Generating new concepts that conformed to a given template
- Transforming concepts from a source template to a destination template

## Getting the Application Jar
Download the jar from the [latest release](https://github.com/IHTSDO/snomed-template-service/releases/latest) page

... or if you would like to build the jar manually use maven:
```bash
mvn clean package
```
Then copy the jar from the target directory.

## General Spring Boot Configuration Advice
The default configuration of this Spring Boot application can be found in [application.properties](src/main/resources/application.properties). The defaults can be overridden by creating an override `application.properties`, file in the same directory as the jar, containing just the options you want to override. Alternatively options can be set using command line arguments, for example set a different HTTP port:
```bash
java -Xmx3g -jar target/snomed-template-service*.jar --server.port=8081
```
For other options see [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html).

The default username and password (user:password) can be changed using the `security.user.name` and `security.user.password` properties.

## Application Setup
Create an application directory to contain the jar file and other configuration files.

Clone the [snomed-templates](https://github.com/IHTSDO/snomed-templates) repository into the application directory. These templates will be loaded by the service to be used for concept authoring and transformations.
To improve performance the templates are cached. **POST /templates/reload** will clear all caches and should be used when new templates are created/
added to the **snomed-templates** store. The snomed-templates store is configured via `templateStorePath` configuration option.

Clone this [snomed-template-service](https://github.com/IHTSDO/snomed-template-service) repository and copy the **transformation-recipes** directory into the application directory.

### Mandatory Configuration
The service needs to connect to the Snowstorm terminology server for querying SNOMED CT concepts using the `terminologyserver.url` configuration option.

### Running the Application
Once setup is complete run the application:
```bash
java -Xmx3g -jar target/snomed-template-service*.jar
```

## Building for Debian/Ubuntu Linux
A Debian package can be created using the 'deb' maven profile. 
```bash
mvn clean package -Pdeb
```
