package org.ihtsdo.otf.authoringtemplate;

import org.springframework.boot.SpringApplication;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
public class App extends Config {

	public static void main(String[] args) {
		System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true"); 
		SpringApplication.run(App.class, args);
	}
}
