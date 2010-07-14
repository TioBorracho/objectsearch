package com.jklas.search.engine.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.jklas.search.engine.dto.ObjectResult;

public class FilterChain {

	private final List<ResultFilter> filters;
	
	public FilterChain(ResultFilter... filter) {
		this(Arrays.asList(filter));
	}	
	
	public FilterChain(List<ResultFilter> filterList) {
		filters = new ArrayList<ResultFilter>(filterList);
	}
	
	public void applyFilters(Collection<? extends ObjectResult> unfilteredResults) {
		for (Iterator<? extends ObjectResult> iterator = unfilteredResults.iterator(); iterator.hasNext();) {
			ObjectResult resultToBeFiltered = (ObjectResult) iterator.next();
			
			for (ResultFilter resultFilter : filters) {
				if( resultFilter.isFiltered(resultToBeFiltered) ) iterator.remove();
			}
		}
	}

}
