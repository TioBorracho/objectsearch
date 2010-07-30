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
