package org.ihtsdo.otf.authoringtemplate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JsonStore {

	private static final String EXTENSION = ".json";
	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final FilenameFilter FILENAME_FILTER = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(EXTENSION);
		}
	};

	private File storeDirectory;

	private final ObjectMapper objectMapper;

	public JsonStore(File storeDirectory, ObjectMapper objectMapper) {
		this.storeDirectory = storeDirectory;
		this.objectMapper = objectMapper;
		if (!storeDirectory.isDirectory()) {
			if (!storeDirectory.mkdirs()) {
				throw new RuntimeException("storeDirectory " + storeDirectory.getAbsolutePath() + " could not be created.");
			}
		}
	}

	public <T> T load(String name, Class<T> clazz) throws IOException {
		final File file = getFile(name, clazz);
		if (file.isFile()) {
			return objectMapper.readValue(file, clazz);
		}
		return null;
	}

	public void save(String name, Object object) throws IOException {
		try (final FileWriter writer = new FileWriter(getFile(name, object.getClass()))) {
			objectMapper.writeValue(writer, object);
		}
	}

	public <T> Set<T> loadAll(Class<T> clazz) throws IOException {
		final File classDir = getClassDir(clazz);
		Set<T> all = new HashSet<>();
		for (File file : classDir.listFiles(FILENAME_FILTER)) {
			all.add(objectMapper.readValue(file, clazz));
		}
		return all;
	}

	private File getFile(String name, Class<?> clazz) {
		return new File(getClassDir(clazz), name + EXTENSION);
	}

	private File getClassDir(Class<?> clazz) {
		final File classDir = new File(storeDirectory, clazz.getSimpleName());
		if (!(classDir.exists() || classDir.mkdirs())) {
			logger.warn("Failed to create directory {}", classDir.getAbsolutePath());
		}
		return classDir;
	}

	public File getStoreDirectory() {
		return storeDirectory;
	}

}
