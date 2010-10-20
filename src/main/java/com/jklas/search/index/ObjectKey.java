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
package com.jklas.search.index;

import java.io.Serializable;

/**
 * This class is inmutable. This invariant shouldn't be
 * modified since the posting lists don't make copies
 * of keys
 * 
 * @author Julián
 *
 */
public class ObjectKey implements Serializable{
	
	private static final long serialVersionUID = 6703442420896212274L;

	private final Class<?> clazz;
	
	private final Serializable id;
	
	public ObjectKey(Class<?> clazz, Serializable id) {
		this.clazz = clazz;
		this.id = id;
	}
	
	public ObjectKey(ObjectKey key) {
		this(key.getClazz(),key.getId());
	}
	
	public Class<?> getClazz() {
		return clazz;
	}
	
	public Serializable getId() {
		return id;
	}
	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.getName().hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectKey other = (ObjectKey) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {	
		return "Class: "+ clazz.toString() + "\nID:" + id.toString();
	}
	
}
