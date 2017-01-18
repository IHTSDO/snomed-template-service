package org.ihtsdo.otf.authoringtemplate;

import org.springframework.boot.SpringApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
public class App extends Config {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
