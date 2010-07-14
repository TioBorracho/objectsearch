package com.jklas.search.engine.filter;

import com.jklas.search.engine.dto.ObjectResult;

public interface ResultFilter {
	
	public boolean isFiltered(ObjectResult filtrable);

}
