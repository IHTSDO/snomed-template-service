package org.ihtsdo.otf.transformationandtemplate.service.client;

import java.util.Collection;
import java.util.Date;

public class AuthoringTask {

	private String key;
	private String projectKey;
	private String summary;
	private String status;
	private TaskUser assignee;
	private Collection<TaskUser> reviewers;

	// When the jira ticket was created
	private Date created;

	// When the jira ticket was updated
	private Date updated;

	private String branchPath;

	private AuthoringTask() {
	}

	public AuthoringTask(String key, String projectKey) {
		this.key = key;
		this.projectKey = projectKey;
	}

	public String getKey() {
		return key;
	}

	public AuthoringTask setKey(String key) {
		this.key = key;
		return this;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public AuthoringTask setProjectKey(String projectKey) {
		this.projectKey = projectKey;
		return this;
	}

	public String getSummary() {
		return summary;
	}

	public AuthoringTask setSummary(String summary) {
		this.summary = summary;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public AuthoringTask setStatus(String status) {
		this.status = status;
		return this;
	}

	public TaskUser getAssignee() {
		return assignee;
	}

	public AuthoringTask setAssignee(TaskUser assignee) {
		this.assignee = assignee;
		return this;
	}

	public Collection<TaskUser> getReviewers() {
		return reviewers;
	}

	public AuthoringTask setReviewers(Collection<TaskUser> reviewers) {
		this.reviewers = reviewers;
		return this;
	}

	public Date getCreated() {
		return created;
	}

	public AuthoringTask setCreated(Date created) {
		this.created = created;
		return this;
	}

	public Date getUpdated() {
		return updated;
	}

	public AuthoringTask setUpdated(Date updated) {
		this.updated = updated;
		return this;
	}

	public String getBranchPath() {
		return branchPath;
	}

	public AuthoringTask setBranchPath(String branchPath) {
		this.branchPath = branchPath;
		return this;
	}
}
