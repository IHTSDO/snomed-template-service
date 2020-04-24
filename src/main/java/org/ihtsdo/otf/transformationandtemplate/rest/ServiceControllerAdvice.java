package org.ihtsdo.otf.transformationandtemplate.rest;

import org.ihtsdo.otf.transformationandtemplate.rest.error.InputError;
import org.ihtsdo.otf.rest.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ServiceControllerAdvice {

	@ResponseBody
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	InputError illegalArgumentExceptionHandler(IllegalArgumentException e) {
		return new InputError(e.getMessage());
	}

	@ResponseBody
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	ResourceNotFoundException notFoundExceptionHandler(ResourceNotFoundException e) {
		return e;
	}

	@ResponseBody
	@ExceptionHandler(InputError.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	InputError inputErrorHandler(InputError e) {
		return e;
	}

}
