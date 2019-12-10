package org.ihtsdo.otf.authoringtemplate.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JsonStore {

	private static final String EXTENSION = ".json";

	private static final FilenameFilter FILENAME_FILTER = (dir, name) -> name.endsWith(EXTENSION);

	private File storeDirectory;

	private final ObjectMapper objectMapper;

	public JsonStore(File storeDirectory, ObjectMapper objectMapper) {
		this.storeDirectory = storeDirectory;
		this.objectMapper = objectMapper;
		if (!storeDirectory.exists()) {
			if (!storeDirectory.mkdirs()) {
				throw new RuntimeException("storeDirectory " + storeDirectory.getAbsolutePath() + " could not be created.");
			}
		}
	}

	public <T> T load(String name, Class<T> clazz) throws IOException {
		final File file = getFile(name);
		if (file.isFile()) {
			return objectMapper.readValue(file, clazz);
		}
		return null;
	}

	public void save(String name, Object object) throws IOException {
		try (final FileWriter writer = new FileWriter(getFile(name))) {
			objectMapper.writeValue(writer, object);
		}
	}

	public <T> Set<T> loadAll(Class<T> clazz) throws IOException {
		Set<T> all = new HashSet<>();
		for (File file : storeDirectory.listFiles(FILENAME_FILTER)) {
			all.add(objectMapper.readValue(file, clazz));
		}
		return all;
	}

	private File getFile(String name) {
		return new File(storeDirectory, name + EXTENSION);
	}

	public File getStoreDirectory() {
		return storeDirectory;
	}

}
