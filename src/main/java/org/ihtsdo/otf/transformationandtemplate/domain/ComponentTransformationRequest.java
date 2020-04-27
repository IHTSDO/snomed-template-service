package org.ihtsdo.otf.transformationandtemplate.domain;

import java.io.InputStream;

public class ComponentTransformationRequest {

	private final String recipe;
	private final InputStream tsvValues;
	private final String branchPath;

	public ComponentTransformationRequest(String recipe, String branchPath, InputStream tsvValues) {
		this.recipe = recipe;
		this.branchPath = branchPath;
		this.tsvValues = tsvValues;
	}

	public String getRecipe() {
		return recipe;
	}

	public String getBranchPath() {
		return branchPath;
	}

	public InputStream getTsvValues() {
		return tsvValues;
	}
}
