package org.ihtsdo.otf.transformationandtemplate.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.nashorn.internal.ir.annotations.Ignore;

import java.io.InputStream;

public class ComponentTransformationRequest {

	private String recipe;
	private InputStream tsvValues;
	private String branchPath;

	// Required for Jackson
	@SuppressWarnings("unused")
	public ComponentTransformationRequest() {
	}

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

	@JsonIgnore
	public InputStream getTsvValues() {
		return tsvValues;
	}

	public void setTsvValues(InputStream tsvValues) {
		this.tsvValues = tsvValues;
	}
}
