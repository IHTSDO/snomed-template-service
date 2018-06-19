package org.ihtsdo.otf.authoringtemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import javax.annotation.PreDestroy;

import org.ihtsdo.otf.authoringtemplate.service.JsonStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.FileSystemUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@PropertySource("classpath:application.properties")
public class TestConfig {

	@Autowired
	private ObjectMapper objectMapper;

	private File tempDir;

	@Bean
	public JsonStore getTemplateJsonStore() throws IOException {
		tempDir = Files.createTempDirectory(UUID.randomUUID().toString()).toFile();
		final JsonStore jsonStore = new JsonStore(tempDir, objectMapper);
		return jsonStore;
	}

	@PreDestroy
	public void deleteTempDirectory() {
		FileSystemUtils.deleteRecursively(tempDir);
	}

}
