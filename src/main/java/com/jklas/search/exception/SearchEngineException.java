package com.jklas.search.exception;

/**
 * <p>
 * Excepción lanzada por las capas externas del motor de búsqueda.
 * </p>
 * 
 * <p>
 * Este es el único tipo de excepción que se debe lanzar hacia el usuario del
 * motor de búsqueda. La única excepción es en el caso de la indexación, donde
 * se debe lanzar una excepción IndexAccessException .
 * </p>
 * 
 * @author Julián
 * @since 1.0
 * @date 2009-07-25
 * @see IndexAccessException
 *
 */
public class SearchEngineException extends Exception {

	private static final long serialVersionUID = 1L;

	private Throwable cause;
	
	private String message;
	
	public SearchEngineException(String message, Throwable cause) {
		this.cause = cause;
		this.message = message;
	}
	
	public SearchEngineException(String message) {
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
