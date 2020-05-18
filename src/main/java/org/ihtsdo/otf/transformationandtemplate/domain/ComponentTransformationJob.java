package org.ihtsdo.otf.transformationandtemplate.domain;

import org.ihtsdo.otf.transformationandtemplate.service.template.TransformationStatus;

import java.util.UUID;

public class ComponentTransformationJob {

	private String id;
	private String user;
	private ComponentTransformationRequest request;
	private StatusAndMessage status;

	public ComponentTransformationJob() {
	}

	public ComponentTransformationJob(ComponentTransformationRequest request, String user) {
		id = UUID.randomUUID().toString();
		this.request = request;
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public String getUser() {
		return user;
	}

	public StatusAndMessage getStatus() {
		return status;
	}

	public void setStatus(StatusAndMessage status) {
		this.status = status;
	}

	public void updateStatus(TransformationStatus status) {
		updateStatus(status, null);
	}
	public void updateStatus(TransformationStatus status, String message) {
		this.status = new StatusAndMessage(status, message);
	}

	public ComponentTransformationRequest getRequest() {
		return request;
	}
}
