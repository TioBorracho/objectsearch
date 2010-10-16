package com.jklas.search;

import com.jklas.search.configuration.SearchConfiguration;

/**
 * 
 * Esta clase debe ser thread-safe
 * 
 * @author Julián
 * 
 */
// TODO Generar un método que cree una configuracion pero que no la active y otro método que active configuraciones 
// TODO Permitir configurar a través de xml y properties
public final class SearchEngine {

	private SearchConfiguration activeConfiguration;
	
	private boolean isConfigured;
	
	private static SearchEngine instance = new SearchEngine();
	
	private SearchEngine(){}
	
	public static SearchEngine getInstance() {		
		return instance;
	}
	
	/**
	 * Retorna una nueva configuración del motor de búsqueda e indexación.
	 * 
	 * @param filename
	 * @return
	 */
	public synchronized SearchConfiguration newConfiguration() {
		activeConfiguration = new SearchConfiguration();
		setConfigured(true);
		return activeConfiguration;
	}

	public synchronized SearchConfiguration getConfiguration() {
		return activeConfiguration;
	}

	public synchronized void reset() {
		setConfigured(false);
		setActiveConfiguration(null);
	}
	
	private void setConfigured(boolean isConfigured) {
		this.isConfigured = isConfigured;
	}

	public boolean isConfigured() {
		return isConfigured;
	}
	
	private void setActiveConfiguration(SearchConfiguration activeConfiguration) {
		this.activeConfiguration = activeConfiguration; 
	}	
	
	@Override
	protected Object clone() throws CloneNotSupportedException {	
		throw new CloneNotSupportedException("Search object is a singleton and can't be cloned");
	}
}
