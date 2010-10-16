package com.jklas.search.sort;

import java.util.Comparator;

import com.jklas.search.engine.dto.ObjectResult;

public class ReverseComparator<E extends ObjectResult> implements Comparator<E> {

	private final Comparator<E> comparator;
	
	public ReverseComparator(Comparator<E> comparator) {
		if(comparator == null) throw new IllegalArgumentException("Can't reverse a null comparator");
		this.comparator = comparator;
	}

	@Override
	public int compare(E o1, E o2) {		
		return -comparator.compare(o1, o2);
	}

	
	
}
