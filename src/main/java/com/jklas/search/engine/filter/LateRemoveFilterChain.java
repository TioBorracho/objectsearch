package com.jklas.search.engine.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.jklas.search.engine.dto.ObjectResult;

public class LateRemoveFilterChain implements FilterChain {

	private final List<ResultFilter> filters;
	
	public LateRemoveFilterChain(ResultFilter... filter) {
		this(Arrays.asList(filter));
	}	
	
	public LateRemoveFilterChain(List<ResultFilter> filterList) {
		filters = new ArrayList<ResultFilter>(filterList);
	}
	
	public void applyFilters(Collection<? extends ObjectResult> unfilteredResults) {
		Set<ObjectResult> removed = new HashSet<ObjectResult>();
		
		for (Iterator<? extends ObjectResult> iterator = unfilteredResults.iterator(); iterator.hasNext();) {
			ObjectResult resultToBeFiltered = (ObjectResult) iterator.next();
			
			for (ResultFilter resultFilter : filters) {
				if( resultFilter.isFiltered(resultToBeFiltered) ) removed.add(resultToBeFiltered);
			}
		}
		
		unfilteredResults.removeAll(removed);
	}

}
