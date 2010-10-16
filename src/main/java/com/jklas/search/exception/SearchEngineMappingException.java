package com.jklas.search.exception;

public class SearchEngineMappingException extends Exception  {

	private static final long serialVersionUID = 1L;

	private Throwable cause;

	private String message;

	public SearchEngineMappingException(String message, Throwable cause) {
		this.cause = cause;
		this.message = message;
	}

	public SearchEngineMappingException(String message) {
		this.message = message;
	}

	@Override
	public Throwable getCause() {		
		return cause;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
