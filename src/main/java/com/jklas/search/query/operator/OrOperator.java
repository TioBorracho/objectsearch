package com.jklas.search.query.operator;

import java.util.Set;

import com.jklas.search.engine.dto.ObjectResult;
import com.jklas.search.index.MasterAndInvertedIndexReader;
import com.jklas.search.index.Term;

public class OrOperator<E extends ObjectResult> extends BinarySimetricOperator<E> {
	
	private static final Term orTerm = new Term("+OR");
	
	public OrOperator(Operator<E> leftOperator, Operator<E> rightOperator) {
		super(leftOperator, rightOperator);	
	}

	public static boolean isOperator(Term candidate) {
		return orTerm.equals(candidate);
	}

	@Override
	public Term getOperatorTerm() {		
		return orTerm;
	}

	@Override
	public Set<E> work(MasterAndInvertedIndexReader reader) {		
		Set<E> leftResults = getLeft().work(reader);
		leftResults.addAll(getRight().work(reader));
		return leftResults;
	}
	
}
