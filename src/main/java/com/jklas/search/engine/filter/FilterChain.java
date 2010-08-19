package com.jklas.search.engine.filter;

import java.util.Collection;

import com.jklas.search.engine.dto.ObjectResult;

public interface FilterChain {

	public abstract void applyFilters(
			Collection<? extends ObjectResult> unfilteredResults);

}