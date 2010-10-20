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
