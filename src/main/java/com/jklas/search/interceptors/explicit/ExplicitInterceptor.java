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
package com.jklas.search.interceptors.explicit;

import java.io.Serializable;

import com.jklas.search.SearchEngine;
import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.index.dto.IndexObject;
import com.jklas.search.interceptors.SearchInterceptor;

/**
 * 
 * Interceptor utilizado en contextos donde
 * no hay un mecanismo interceptor de los 
 * eventos del ORM
 * 
 * @author Julián
 * 
 */
public class ExplicitInterceptor {

	private static SearchInterceptor searchInterceptor ;
	
	@SuppressWarnings("unused")
	private ExplicitInterceptor() { }
	
	public ExplicitInterceptor(SearchInterceptor interceptor ) {
		searchInterceptor = interceptor;
	}
	

	public boolean save(Object entity, Serializable id) {
		try {
			if(!isMapped(entity)) {				
				return false;
			}
			
			searchInterceptor.createOrUpdate(new IndexObject(entity, id));
			return true;
		} catch (IndexObjectException e) {
//			LogFactory.getLog(getClass()).error("Error al indexar. Entidad: "+entity+" - id:"+id,e);
			return false;
		}				
	}
		
	public boolean delete(Object entity, Serializable id) {
		try {
			if(!isMapped(entity)) {				
				return false;
			}
			
			searchInterceptor.delete(new IndexObject(entity, id));
			
			return true;
		} catch (IndexObjectException e) {
//			LogFactory.getLog(getClass()).error("Error al indexar. Entidad: "+entity+" - id:"+id,e);
			return false;
		}
	}
	

	public boolean update(Object entity, Serializable id) {
		try {
			if(!isMapped(entity)) {				
				return false;
			}
			searchInterceptor.update(new IndexObject(entity, id));
			return true;
		} catch (IndexObjectException e) {
	//		LogFactory.getLog(getClass()).error("Error al indexar. Entidad: "+entity+" - id:"+id,e);
			return false;
		}				
	}
	
	
	/**
	 * Object Create/Insertion event. 
	 * 
	 * @param entity the new domain model entity
	 * @param id object's serializable identifier
	 * @return true if the object was indexed succesfully, false otherwise
	 */
	public boolean create(Object entity, Serializable id) {
		try {			
			if(!isMapped(entity)) {				
				return false;
			}
			
			searchInterceptor.create(new IndexObject(entity, id));
			
			return true;
		} catch (IndexObjectException e) {
			//LogFactory.getLog(getClass()).error("Error al indexar. Entidad: "+entity+" - id:"+id,e);
			return false;
		}				
	}
	
	/**
	 * Checks is an object is mapped into the current active configuration.
	 * This is usefull to avoid sending unindexable objects thru
	 * the pipeline.
	 * 
	 * @param entity the entity being checked against the configuration
	 * @return true if the object is indexed, false otherwise.
	 */
	private boolean isMapped(Object entity) {
		SearchEngine search = SearchEngine.getInstance();
		return search.isConfigured() && search.getConfiguration().isMapped(entity.getClass());
	}

}
