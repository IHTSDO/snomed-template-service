package org.ihtsdo.otf.transformationandtemplate.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.InputStream;

public class ComponentTransformationRequest {

	private String recipe;
	private InputStream tsvValues;
	private String branchPath;
	private String projectKey;
	private String taskTitle;

	// The number of changes per task.
	private int batchSize;
	private String taskAssignee;
	private String taskReviewer;

	// Required for Jackson
	@SuppressWarnings("unused")
	public ComponentTransformationRequest() {
	}

	public ComponentTransformationRequest(String recipe, String branchPath, String projectKey,
			String taskTitle, String taskAssignee, String taskReviewer, int batchSize, InputStream tsvValues) {
		this.recipe = recipe;
		this.branchPath = branchPath;
		this.projectKey = projectKey;
		this.taskTitle = taskTitle;
		this.taskAssignee = taskAssignee;
		this.taskReviewer = taskReviewer;
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

	public String getTaskAssignee() {
		return taskAssignee;
	}

	public String getTaskReviewer() {
		return taskReviewer;
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

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}
}
