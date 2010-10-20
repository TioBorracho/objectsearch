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
package com.jklas.search.query.operator;

import java.util.Set;

import com.jklas.search.engine.dto.ObjectResult;
import com.jklas.search.index.MasterAndInvertedIndexReader;
import com.jklas.search.index.PostingList;
import com.jklas.search.index.Term;
import com.jklas.search.query.PostingListExtractor;

public class RetrieveOperator<E extends ObjectResult> extends Operator<E> {

	private final Term term;
	
	private final PostingListExtractor<E> extractor;
	
	public RetrieveOperator(Term term, PostingListExtractor<E> extractor) {
		this.term = term;
		this.extractor = extractor;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other==this) return true;
		
		if(other==null) return false;
		
		if(getClass()!=other.getClass()) return false;
		
		RetrieveOperator<?> o = (RetrieveOperator<?>)other;
		
		return term.equals(o.getOperatorTerm());
	}

	public Term getOperatorTerm() {
		return term;
	}
	
	@Override
	public int hashCode() {		
		return term.hashCode();
	}

	@Override
	public String toString() {	
		return "R("+term+")";
	}

	@Override
	public Set<E> work(MasterAndInvertedIndexReader reader) {		
		PostingList postingList = reader.read(getOperatorTerm());
		
		return extractor.extract(getOperatorTerm(),postingList);
	}
}
