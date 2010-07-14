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
