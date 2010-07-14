package com.jklas.search.engine.dto;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import com.jklas.search.index.ObjectKey;


/**
 * Dehidrated result for indexed objects 
 * 
 * @author Julián Klas
 * @since 1.0
 * @date 2009-08-01
 */
public abstract class ObjectResult {
	
	private final ObjectKey objectKey;
	
	private final Map<Field, Object> storedFields;
	
	@SuppressWarnings("unchecked")
	public ObjectResult(ObjectKey objectKey) {
		this(objectKey, Collections.EMPTY_MAP);
	}
	
	public ObjectResult(ObjectKey objectKey, Map<Field, Object> storedFields) {
		this.objectKey = objectKey;
		this.storedFields = storedFields;
	}

	public ObjectKey getKey() {
		return objectKey;
	}

	public Map<Field, Object> getStoredFields() {
		return storedFields;
	}
	
	@Override
	public String toString() {
		return "Posting: " + getKey(); 
	}
	
	public abstract int hashCode();
	
	public abstract boolean equals(Object obj);
}
