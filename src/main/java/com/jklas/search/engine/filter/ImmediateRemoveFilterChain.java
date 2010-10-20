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
