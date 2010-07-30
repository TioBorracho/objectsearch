package com.jklas.search.sort;

import java.util.Collection;
import java.util.List;

import com.jklas.search.engine.dto.ObjectResult;

public interface PreSort {

	public List<? extends ObjectResult> work(Collection<? extends ObjectResult> currentObjects);
	
}
