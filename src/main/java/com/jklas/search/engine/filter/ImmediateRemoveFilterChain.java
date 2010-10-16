package com.jklas.search.engine.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.jklas.search.engine.dto.ObjectResult;

public class ImmediateRemoveFilterChain implements FilterChain {

	private final List<ResultFilter> filters;
	
	public ImmediateRemoveFilterChain(ResultFilter... filter) {
		this(Arrays.asList(filter));
	}	
	
	public ImmediateRemoveFilterChain(List<ResultFilter> filterList) {
		filters = new ArrayList<ResultFilter>(filterList);
	}
	
	/* (non-Javadoc)
	 * @see com.jklas.search.engine.filter.FilterChain#applyFilters(java.util.Collection)
	 */
	@Override
	public void applyFilters(Collection<? extends ObjectResult> unfilteredResults) {
		for (Iterator<? extends ObjectResult> iterator = unfilteredResults.iterator(); iterator.hasNext();) {
			ObjectResult resultToBeFiltered = (ObjectResult) iterator.next();
			
			for (ResultFilter resultFilter : filters) {
				if( resultFilter.isFiltered(resultToBeFiltered) ) iterator.remove();
			}
		}
	}

}
