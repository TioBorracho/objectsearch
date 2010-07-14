package com.jklas.search.query.bool;

import com.jklas.search.engine.dto.ObjectKeyResult;
import com.jklas.search.query.SearchQuery;
import com.jklas.search.query.operator.Operator;


public class BooleanQuery extends SearchQuery {
	
	private final Operator<ObjectKeyResult> rootOperator;
	
	public BooleanQuery(Operator<ObjectKeyResult> root) {
		this.rootOperator = root;
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
		BooleanQuery other = (BooleanQuery) obj;
		if (rootOperator == null) {
			if (other.rootOperator != null)
				return false;
		} else if (!rootOperator.equals(other.rootOperator))
			return false;
		return true;
	}

	@Override
	public String toString() {		
		return rootOperator.toString();
	}

	public Operator<ObjectKeyResult> getRootOperator() {		
		return rootOperator;
	}
	
}
