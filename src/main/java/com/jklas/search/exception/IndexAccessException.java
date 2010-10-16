package com.jklas.search.exception;

/**
 * Excepción para los accesos a los índices, típicamente lanzada
 * desde las capas DAO.
 * 
 * @author Julián Klas
 * @since 1.0
 * @date 2009-07-25
 *
 */
public class IndexAccessException extends Exception {

	private static final long serialVersionUID = 1L;

	private Throwable cause;
	
	private String message;
	
	public IndexAccessException(String message, Throwable cause) {
		this.cause = cause;
		this.message = message;
	}
	
	public IndexAccessException(String message) {
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
