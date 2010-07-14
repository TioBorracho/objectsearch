package com.jklas.search.configuration;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SearchConfiguration {

	private Map<Class<?>,SearchMapping> mappings = new ConcurrentHashMap<Class<?>,SearchMapping>();
 
	
	public SearchMapping addEmptyMapping(Class<?> classToMap) {		
		SearchMapping newMapping = new SearchMapping();
		mappings.put(classToMap, newMapping);
		return newMapping;
	}


	public boolean isMapped(Class<?> clazz) {		
		return mappings.containsKey(clazz);
	}
	
	public boolean isMapped(Object object) {
		if(object==null) return false;
		
		Class<?> clazz = getClassForObject(object);
		return isMapped(clazz);
	}

	public boolean isMapped(Class<?> clazz,	Field field) {
		return isMapped(clazz) && getMapping(clazz).isMapped(field);
	}

	public SearchMapping getMapping(Object entity) {
		return mappings.get(entity.getClass());
	}
	
	public SearchMapping getMapping(Class<?> clazz) {				
		return mappings.get(clazz);
	}


	private Class<?> getClassForObject(Object object) {		
		Class<?> clazz = object.getClass();
		return clazz;
	}

	/**
	 * Removes a mapping for a class
	 * 
	 * @param clazz the class to be unmapped
	 */
	public void removeMap(Class<?> clazz) {
		mappings.remove(clazz);
	}

}
