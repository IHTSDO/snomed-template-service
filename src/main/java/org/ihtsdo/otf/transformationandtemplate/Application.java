package org.ihtsdo.otf.transformationandtemplate;

import org.apache.tomcat.util.buf.EncodedSolidusHandling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@ComponentScan(basePackages = {
		"org.ihtsdo.otf.transformationandtemplate",
		"org.snomed.otf.scheduler.domain",
		"org.snomed.otf.script"})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	@Bean
	public TomcatConnectorCustomizer connectorCustomizer() {
		// Swagger encodes the slash in branch paths
		return connector -> connector.setEncodedSolidusHandling(EncodedSolidusHandling.DECODE.getValue());
	}

}
