package com.jklas.search.query.operator;

import java.util.Set;

import com.jklas.search.engine.dto.ObjectResult;
import com.jklas.search.index.MasterAndInvertedIndexReader;
import com.jklas.search.index.Term;

public class AndOperator<E extends ObjectResult> extends BinarySimetricOperator<E> {
	
	private static final Term andTerm = new Term("+AND");
		
	public AndOperator(Operator<E> leftOperator, Operator<E> rightOperator) {
		super(leftOperator, rightOperator);	
	}
	
	public static boolean isOperator(Term candidate) {
		return andTerm.equals(candidate);
	}
	
	@Override
	public Term getOperatorTerm() {	
		return andTerm;
	}

	@Override
	public Set<E> work(MasterAndInvertedIndexReader reader) {
		Set<E> leftResults = getLeft().work(reader);
		leftResults.retainAll(getRight().work(reader));
		return leftResults;
	}
	
}
