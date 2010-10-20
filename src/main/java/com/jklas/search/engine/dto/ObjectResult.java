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
