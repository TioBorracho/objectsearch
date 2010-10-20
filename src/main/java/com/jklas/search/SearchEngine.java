/**
 * Object Search Framework
 *
 * Copyright (C) 2010 Julian Klas
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
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
