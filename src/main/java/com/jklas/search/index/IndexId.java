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

public class IndexId implements Serializable {

	private static final long serialVersionUID = -4357142232120086999L;
	
	public final static String DEFAULT_INDEX_NAME = "";
	
	private final static IndexId DEFAULT_INDEX_ID = new IndexId(DEFAULT_INDEX_NAME);
	
	private final String indexName;	
	
	public IndexId(String indexName) {
		this.indexName = indexName;
	}
	
	public String getIndexName() {
		return indexName;
	}
	
	public static IndexId getDefaultIndexId() {
		return DEFAULT_INDEX_ID;
	}

	@Override
	public int hashCode() {		
		return ((indexName == null) ? 0 : indexName.hashCode());		
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexId other = (IndexId) obj;
		if (indexName == null) {
			if (other.indexName != null)
				return false;
		} else if (!indexName.equals(other.indexName))
			return false;
		return true;
	}
	
	@Override
	public String toString() {	
		return getIndexName();
	}
}
