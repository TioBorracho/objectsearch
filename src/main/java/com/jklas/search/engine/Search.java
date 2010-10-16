package com.jklas.search.engine;

import java.util.Collection;
import java.util.Comparator;

import com.jklas.search.engine.dto.ObjectResult;
import com.jklas.search.engine.filter.FilterChain;
import com.jklas.search.sort.PreSort;

public interface Search {

	public abstract Collection<? extends ObjectResult> search();

	public abstract Collection<? extends ObjectResult> search(FilterChain filterChain);

	public abstract Collection<? extends ObjectResult> search(Comparator<? super ObjectResult> comparator);

	public abstract Collection<? extends ObjectResult> search(PreSort rule);

}