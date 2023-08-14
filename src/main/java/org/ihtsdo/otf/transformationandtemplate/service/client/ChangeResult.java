package org.ihtsdo.otf.transformationandtemplate.service.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.SnomedComponent;

import static java.lang.String.format;

@JsonPropertyOrder({"success", "id", "message", "component"})
public class ChangeResult<T extends SnomedComponent> {

	private Boolean success;
	private String message;
	private T component;

	@SuppressWarnings("unused")// For Jackson
	public ChangeResult() {
	}

	public ChangeResult(T component) {
		this.component = component;
	}

	public ChangeResult(boolean success, T component, String message) {
		this.success = success;
		this.component = component;
		this.message = message;
	}

	public void fail(String message) {
		success = false;
		this.message = message;
	}

	public void addWarning(String message) {
		this.message = format("Warning: %s. %s", message, this.message != null ? this.message : "");
	}

	public void success() {
		success = true;
	}

	public Boolean getSuccess() {
		return success;
	}

	public String id() {
		return component != null ? component.getId() : null;
	}

	public String getMessage() {
		return message;
	}

	@JsonIgnore
	public String getMessageOrEmpty() {
		return message != null ? message : "";
	}

	public T getComponent() {
		return component;
	}

	@Override
	public String toString() {
		return "ChangeResult{" +
				"success=" + success +
				", message='" + message + '\'' +
				", component=" + component +
				'}';
	}
}
