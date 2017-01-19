package org.ihtsdo.otf.authoringtemplate.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends ServiceException {
	public ResourceNotFoundException(String resourceType, String resourceIdentifier) {
		this(resourceType, resourceIdentifier, null);
	}
	public ResourceNotFoundException(String resourceType, String resourceIdentifier, Throwable cause) {
		super("Resource of type " + resourceType + " and ID " + resourceIdentifier + " was not found.", cause);
	}

	@Override
	@ResponseBody
	public String getMessage() {
		return super.getMessage();
	}
}
