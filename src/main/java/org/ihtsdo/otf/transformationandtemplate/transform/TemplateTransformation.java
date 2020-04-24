package org.ihtsdo.otf.transformationandtemplate.transform;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class TemplateTransformation {

	private String transformationId;
	private TransformationStatus status;
	private String branchPath;
	private String errorMsg;
	private Date createdDate;
	private Date lastUpdatedDate;
	private TemplateTransformRequest transformRequest;

	public TemplateTransformation(String branchPath, TemplateTransformRequest transformRequest) {
		this.branchPath = branchPath;
		this.transformRequest = transformRequest;
		transformationId = UUID.randomUUID().toString();
		this.createdDate = Calendar.getInstance().getTime();
	}

	public String getTransformationId() {
		return this.transformationId;
	}

	public String getBranchPath() {
		return this.branchPath;
	}

	public TemplateTransformRequest getTransformRequest() {
		return this.transformRequest;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}

	public void setTransformRequest(TemplateTransformRequest transformRequest) {
		this.transformRequest = transformRequest;
	}

	public void setTransformationId(String transformationId) {
		this.transformationId = transformationId;
	}

	public void setStatus(TransformationStatus status) {
		this.status = status;
	}

	public TransformationStatus getStatus() {
		return status;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TemplateTransformation [");
		if (branchPath != null)
			builder.append("branchPath=").append(branchPath).append(", ");
		if (transformRequest != null)
			builder.append("transformRequest=").append(transformRequest).append(", ");
		if (transformationId != null)
			builder.append("transformationId=").append(transformationId).append(", ");
		if (createdDate != null)
			builder.append("createdDate=").append(createdDate).append(", ");
		if (status != null)
			builder.append("status=").append(status).append(", ");
		if (errorMsg != null)
			builder.append("errorMsg=").append(errorMsg).append(", ");
		if (lastUpdatedDate != null)
			builder.append("lastUpdatedDate=").append(lastUpdatedDate);
		builder.append("]");
		return builder.toString();
	}
}
