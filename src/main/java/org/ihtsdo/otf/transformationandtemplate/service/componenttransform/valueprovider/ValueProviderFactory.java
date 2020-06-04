package org.ihtsdo.otf.transformationandtemplate.service.componenttransform.valueprovider;

import org.ihtsdo.otf.rest.exception.BusinessServiceRuntimeException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class ValueProviderFactory {

	private static final Pattern TSV_FIELD_INDEX_PATTERN = Pattern.compile("\\$tsv.([0-9]{1,2})");
	private static final String MAP_CONSTANT = "map-constant";
	public static final String CONSTANTS_MAP_TSV = "constants-map.tsv";

	private static Map<String, String> constantMap;

	public ValueProvider getProvider(String mapping) {
		if (mapping == null) {
			throw new BusinessServiceRuntimeException("Mapping is null.");
		}
		String[] split = mapping.split("\\|");

		ValueProvider valueProvider = null;
		for (String part : split) {
			Matcher matcher = TSV_FIELD_INDEX_PATTERN.matcher(part);
			if (matcher.matches()) {
				valueProvider = new TSVValueProvider(Integer.parseInt(matcher.group(1)));
			} else if (part.equals(MAP_CONSTANT)) {
				if (valueProvider == null) {
					throw new BusinessServiceRuntimeException(format("%s can only be used after another value provider.", MAP_CONSTANT));
				}
				valueProvider = new MapConstantValueProvider(valueProvider, getConstantMap());
			}
		}
		return valueProvider;
	}

	private Map<String, String> getConstantMap() {
		if (constantMap == null) {
			throw new BusinessServiceRuntimeException("Constant map not yet loaded");
		}
		return constantMap;
	}

	public static void loadConstantMap(String transformationRecipeStorePath) throws IOException {
		File constantsFile = new File(new File(transformationRecipeStorePath), CONSTANTS_MAP_TSV);
		if (!constantsFile.isFile()) {
			throw new BusinessServiceRuntimeException(format("%s file is not in configured transformation recipe store %s.", CONSTANTS_MAP_TSV, transformationRecipeStorePath));
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(constantsFile)))) {
			// Discard header line
			reader.readLine();

			Map<String, String> map = new HashMap<>();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}
				String[] columns = line.split("\t", 2);
				map.put(columns[0], columns[1]);
			}
			constantMap = map;
		}
	}

}
