package com.jklas.search.interceptors.explicit;

import java.io.Serializable;

import com.jklas.search.SearchEngine;
import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.index.dto.IndexObjectDto;
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
			
			searchInterceptor.createOrUpdate(new IndexObjectDto(entity, id));
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
			
			searchInterceptor.delete(new IndexObjectDto(entity, id));
			
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
			searchInterceptor.update(new IndexObjectDto(entity, id));
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
			
			searchInterceptor.create(new IndexObjectDto(entity, id));
			
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
