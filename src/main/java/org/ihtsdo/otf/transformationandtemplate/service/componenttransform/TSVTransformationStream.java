package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import org.ihtsdo.otf.transformationandtemplate.domain.TransformationRecipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class TSVTransformationStream implements TransformationStream {

	private final Map<String, Object> fieldMap;
	private final BufferedReader bufferedReader;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private boolean readFirst;

	public TSVTransformationStream(TransformationRecipe recipe, BufferedReader bufferedReader) {
		this.fieldMap = recipe.getFieldMap();
		this.bufferedReader = bufferedReader;
	}

	@Override
	public ComponentTransformation next() throws IOException {
		if (!readFirst) {
			// Discard header line
			bufferedReader.readLine();
			readFirst = true;
		}
		String line = bufferedReader.readLine();
		if (line == null) {
			return null;
		}
		String[] columns = line.split("\\t");
		return new TSVComponentTransformation(fieldMap, columns);
	}

	@Override
	public void close() {
		try {
			bufferedReader.close();
		} catch (IOException e) {
			logger.error("Failed to close input stream.", e);
		}
	}

}
