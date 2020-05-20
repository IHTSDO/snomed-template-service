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

	public String getKey() {
		return key;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public String getSummary() {
		return summary;
	}

	public String getStatus() {
		return status;
	}

	public TaskUser getAssignee() {
		return assignee;
	}

	public Collection<TaskUser> getReviewers() {
		return reviewers;
	}

	public Date getCreated() {
		return created;
	}

	public Date getUpdated() {
		return updated;
	}

	public String getBranchPath() {
		return branchPath;
	}
}
