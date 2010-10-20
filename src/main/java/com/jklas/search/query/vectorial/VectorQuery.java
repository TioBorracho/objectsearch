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
package com.jklas.search.query.vectorial;

import java.util.Map;
import java.util.Set;

import com.jklas.search.engine.dto.SingleTermObjectResult;
import com.jklas.search.index.IndexId;
import com.jklas.search.index.Term;
import com.jklas.search.query.SearchQuery;
import com.jklas.search.query.operator.Operator;

public class VectorQuery extends SearchQuery {

	private final VectorPostingListExtractor extractor;
	
	private final Operator<SingleTermObjectResult> rootOperator;

	private final Map<Term,Integer> termCount;

	private IndexId selectedIndex;
	
	public VectorQuery(Map<Term,Integer> termCountMap, Operator<SingleTermObjectResult> rootOperator, VectorPostingListExtractor extractor) {		
		this(IndexId.getDefaultIndexId(), termCountMap, rootOperator, extractor);
	}

	public VectorQuery(IndexId selectedIndex, Map<Term,Integer> termCountMap, Operator<SingleTermObjectResult> rootOperator, VectorPostingListExtractor extractor) {		
		this.selectedIndex = selectedIndex;
		this.rootOperator = rootOperator;
		this.termCount = termCountMap;
		this.extractor = extractor;
	}
	
	public Operator<SingleTermObjectResult> getRootOperator() {
		return rootOperator;
	}

	public Map<Term,Integer> getTermVectors() {
		return termCount;
	}

	public Set<Term> getTerms() {
		return termCount.keySet();
	}

	public VectorPostingListExtractor getExtractor() {
		return extractor;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((rootOperator == null) ? 0 : rootOperator.hashCode());
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
		VectorQuery other = (VectorQuery) obj;
		if (rootOperator == null) {
			if (other.rootOperator != null)
				return false;
		} else if (!rootOperator.equals(other.rootOperator))
			return false;
		return true;
	}

	public IndexId getSelectedIndex() {		
		return this.selectedIndex;
	}

	public void setSelectedIndex(IndexId selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

}
