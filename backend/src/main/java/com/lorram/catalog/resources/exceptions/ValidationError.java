package com.lorram.catalog.resources.exceptions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandardError{
	private static final long serialVersionUID = 1L;

	List<FieldMessage> errors = new ArrayList<>();

	public ValidationError(Instant timeStamp, Integer status, String error, String message, String path) {
		super(timeStamp, status, error, message, path);
	}

	public List<FieldMessage> getErrors() {
		return errors;
	}
	
	public void addError(String fieldMessage, String message) {
		errors.add(new FieldMessage(fieldMessage, message));
	}
	
}
