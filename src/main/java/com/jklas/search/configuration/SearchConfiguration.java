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
