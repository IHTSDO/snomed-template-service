package org.ihtsdo.otf.authoringtemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ihtsdo.otf.authoringtemplate.service.JsonStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
public class App {

	@Autowired
	private ObjectMapper objectMapper;

	@Bean
	public JsonStore getTemplateJsonStore() {
		final JsonStore jsonStore = new JsonStore(new File("template-store"), objectMapper);
		return jsonStore;
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
