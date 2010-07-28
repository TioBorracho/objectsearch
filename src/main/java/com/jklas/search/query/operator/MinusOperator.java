package com.jklas.search.query.operator;

import java.util.Set;

import com.jklas.search.engine.dto.ObjectResult;
import com.jklas.search.index.MasterAndInvertedIndexReader;
import com.jklas.search.index.Term;

public class MinusOperator<E extends ObjectResult> extends BinaryOperator<E> {
	
	private static final Term notTerm = new Term("+NOT");
		
	public MinusOperator(Operator<E> results, Operator<E> elementsToBeRemoved) {
		super(results, elementsToBeRemoved);	
	}
	
	public static boolean isOperator(Term candidate) {
		return notTerm.equals(candidate);
	}
	
	@Override
	public Term getOperatorTerm() {	
		return notTerm;
	}

	@Override
	public Set<E> work(MasterAndInvertedIndexReader reader) {
		Set<E> results = getLeft().work(reader);
		Set<E> elementsToBeRemoved = getRight().work(reader);
		
		results.removeAll(elementsToBeRemoved);
		return results;
	}
	
}
