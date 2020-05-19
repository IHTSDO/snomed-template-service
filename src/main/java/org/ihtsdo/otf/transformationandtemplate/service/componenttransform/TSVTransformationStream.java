package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import org.ihtsdo.otf.rest.exception.BadRequestException;
import org.ihtsdo.otf.transformationandtemplate.domain.TransformationRecipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static java.lang.String.format;

public class TSVTransformationStream implements TransformationStream {

	private final Map<String, Object> fieldMap;
	private final BufferedReader bufferedReader;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public TSVTransformationStream(TransformationRecipe recipe, BufferedReader bufferedReader) throws IOException, BadRequestException {
		this.fieldMap = recipe.getFieldMap();
		this.bufferedReader = bufferedReader;

		// Read and verify header line
		String header = bufferedReader.readLine();
		String[] columns = header.split("\\t");
		Collection<String> fieldNames = recipe.getFieldNames();
		if (columns.length != fieldNames.size()) {
			throw new BadRequestException(format("First line of TSV file must contain the list of fields. " +
					"For this transformation %s fields are expected (%s) but found %s.", fieldNames.size(), fieldNames, columns.length));
		}
	}

	@Override
	public ComponentTransformation next() throws IOException {
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
