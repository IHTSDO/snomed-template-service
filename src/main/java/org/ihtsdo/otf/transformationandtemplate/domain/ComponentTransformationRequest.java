package org.ihtsdo.otf.transformationandtemplate.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.InputStream;

public class ComponentTransformationRequest {

	private String recipe;
	private InputStream tsvValues;
	private String branchPath;
	private String projectKey;
	private String taskTitle;
	private int batchSize;

	// Required for Jackson
	@SuppressWarnings("unused")
	public ComponentTransformationRequest() {
	}

	public ComponentTransformationRequest(String recipe, String branchPath, String projectKey, String taskTitle, int batchSize, InputStream tsvValues) {
		this.recipe = recipe;
		this.branchPath = branchPath;
		this.projectKey = projectKey;
		this.taskTitle = taskTitle;
		this.batchSize = batchSize;
		this.tsvValues = tsvValues;
	}

	public String getRecipe() {
		return recipe;
	}

	public String getBranchPath() {
		return branchPath;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public String getTaskTitle() {
		return taskTitle;
	}

	public int getBatchSize() {
		return batchSize;
	}

	@JsonIgnore
	public InputStream getTsvValues() {
		return tsvValues;
	}

	public void setTsvValues(InputStream tsvValues) {
		this.tsvValues = tsvValues;
	}
}
