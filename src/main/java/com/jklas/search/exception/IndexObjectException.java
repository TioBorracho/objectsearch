package com.jklas.search.exception;

/**
 * 
 * <p>
 * Excepción para las capas de indexación de un objeto.
 * </p>
 * 
 * <p>
 * Esta es la única excepción que se debe lanzar hacia los
 * usuarios del servicio de indexación de objetos.
 * </p> 
 * 
 * @author Julián Klas
 * @since 1.0
 * @date 2009-07-26
 * @see SearchEngineException
 *
 */
public class IndexObjectException extends Exception {

	private static final long serialVersionUID = 1L;

	private Throwable cause;
	
	private String message;
	
	public IndexObjectException(String message, Throwable cause) {
		this.cause = cause;
		this.message = message;
	}
	
	public IndexObjectException(String message) {
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
