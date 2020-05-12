package org.ihtsdo.otf.transformationandtemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import javax.annotation.PreDestroy;

import org.ihtsdo.otf.transformationandtemplate.service.JsonStore;
import org.ihtsdo.otf.transformationandtemplate.service.template.TemplateStore;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileSystemUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public abstract class AbstractTest {

	@Autowired
	protected TemplateStore templateStore;

	@TestConfiguration
	private static class TestConfig {

		@Autowired
		private ObjectMapper objectMapper;

		private File tempDir;

		@Bean
		public JsonStore getTemplateJsonStore() throws IOException {
			tempDir = Files.createTempDirectory(UUID.randomUUID().toString()).toFile();
			return new JsonStore(tempDir, objectMapper);
		}

		@PreDestroy
		public void deleteTempDirectory() {
			FileSystemUtils.deleteRecursively(tempDir);
		}
	}


	@Before
	public void before() {
		SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("", ""));
		templateStore.clear();
	}

	@After
	public void after() {
		// Recreate empty template store
		FileSystemUtils.deleteRecursively(templateStore.getTemplateJsonStore().getStoreDirectory());
		templateStore.getTemplateJsonStore().getStoreDirectory().mkdirs();
	}
}
