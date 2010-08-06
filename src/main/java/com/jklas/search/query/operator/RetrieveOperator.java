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
