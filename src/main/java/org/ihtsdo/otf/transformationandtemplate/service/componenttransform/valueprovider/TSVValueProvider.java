package org.ihtsdo.otf.transformationandtemplate.service.componenttransform.valueprovider;

public class TSVValueProvider implements ValueProvider {

	private final int tsvIndex;

	public TSVValueProvider(int tsvIndex) {
		this.tsvIndex = tsvIndex;
	}

	@Override
	public String getValue(String[] columns) {
		if (tsvIndex > -1 && columns.length > tsvIndex) {
            return columns[tsvIndex];
		}
		return null;
	}

}
