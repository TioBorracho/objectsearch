package com.jklas.search.engine.filter;

import com.jklas.search.engine.dto.ObjectResult;

public class ClassFilter implements ResultFilter {
	
	private final Class<?> filterClazz;
	
	private final boolean allowSubclasses ;

	public ClassFilter(Class<?> filterClazz, boolean allowSubclasses) {
		if(filterClazz==null)
			throw new IllegalArgumentException("The clazz to be used for filtering can't be null");

		this.filterClazz = filterClazz;
		
		this.allowSubclasses = allowSubclasses;
	}

	
	public ClassFilter(Class<?> filterClazz) {
		if(filterClazz==null)
			throw new IllegalArgumentException("The clazz to be used for filtering can't be null");

		this.filterClazz = filterClazz;
		this.allowSubclasses = true;
	}
	
	public boolean isFiltered(ObjectResult filtrable) {
		
		if(allowSubclasses) {
			return !filterClazz.isAssignableFrom(filtrable.getKey().getClazz());			
		} else {
			return !filterClazz.equals(filtrable.getKey().getClazz());
		}
		
	}
}
