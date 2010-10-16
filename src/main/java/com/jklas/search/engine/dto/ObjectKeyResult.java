package com.jklas.search.engine.dto;

import java.lang.reflect.Field;
import java.util.Map;

import com.jklas.search.index.ObjectKey;

public class ObjectKeyResult extends ObjectResult {
		
	public ObjectKeyResult(ObjectKey objectKey) {
		super(objectKey);		
	}

	public ObjectKeyResult(ObjectKey objectKey, Map<Field,Object> storedFields) {
		super(objectKey, storedFields);
	}
	
	@Override
	public int hashCode() {	
		return getKey().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==this) return true;
		
		if(obj == null) return false;
		
		if(obj.getClass()!=getClass()) return false;
		
		return getKey().equals(((ObjectKeyResult)obj).getKey());
	}
	
}
