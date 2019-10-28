package org.ihtsdo.otf.authoringtemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import javax.annotation.PreDestroy;

import org.ihtsdo.otf.authoringtemplate.service.JsonStore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileSystemUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public abstract class AbstractTest {
	
	@TestConfiguration
	private static class TestConfig {
		
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
}
