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
package com.jklas.search.query.bool;

import com.jklas.search.engine.dto.ObjectKeyResult;
import com.jklas.search.index.IndexId;
import com.jklas.search.query.SearchQuery;
import com.jklas.search.query.operator.Operator;


public class BooleanQuery extends SearchQuery {
	
	private final Operator<ObjectKeyResult> rootOperator;
	
	private final IndexId selectedIndex;
	
	public BooleanQuery(Operator<ObjectKeyResult> root, IndexId indexId) {
		this.rootOperator = root;
		this.selectedIndex = indexId;
	}
	
	public BooleanQuery(Operator<ObjectKeyResult> root) {
		this(root, IndexId.getDefaultIndexId());
	}

	@Override
	public String toString() {		
		return rootOperator.toString();
	}

	public Operator<ObjectKeyResult> getRootOperator() {		
		return rootOperator;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((rootOperator == null) ? 0 : rootOperator.hashCode());
		result = prime * result
				+ ((selectedIndex == null) ? 0 : selectedIndex.hashCode());
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
		BooleanQuery other = (BooleanQuery) obj;
		if (rootOperator == null) {
			if (other.rootOperator != null)
				return false;
		} else if (!rootOperator.equals(other.rootOperator))
			return false;
		if (selectedIndex == null) {
			if (other.selectedIndex != null)
				return false;
		} else if (!selectedIndex.equals(other.selectedIndex))
			return false;
		return true;
	}
	
	public IndexId getSelectedIndex() {
		return selectedIndex;
	}
	
}
