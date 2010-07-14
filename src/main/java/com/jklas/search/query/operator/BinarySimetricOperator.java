package com.jklas.search.query.operator;

import com.jklas.search.engine.dto.ObjectResult;
import com.jklas.search.index.Term;



public abstract class BinarySimetricOperator<E extends ObjectResult> extends Operator<E> {
	
	private final Operator<E> leftOperator;
	
	private final Operator<E> rightOperator;
	
	public BinarySimetricOperator(Operator<E> leftOperator, Operator<E> rightOperator) {
		if(leftOperator == null || rightOperator == null) throw new IllegalArgumentException("Can't accept null operators... leafs must have unary operators");
		this.leftOperator = leftOperator;
		this.rightOperator = rightOperator;
	}
	
	public Operator<E> getLeft() {
		return leftOperator;
	}
	
	public Operator<E> getRight() {
		return rightOperator;
	}

	@Override
	public int hashCode() {		
		return 31 * (leftOperator.hashCode()+rightOperator.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if(obj == null) return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		BinarySimetricOperator<?> other = (BinarySimetricOperator<?>) obj;
		
		return (leftOperator.equals(other.leftOperator) && rightOperator.equals(other.rightOperator)) ||
			(leftOperator.equals(other.rightOperator) && rightOperator.equals(other.leftOperator));		
	}
	
	public abstract Term getOperatorTerm();
	
	@Override
	public String toString() {	
		return "("+getLeft().toString()+" "+getOperatorTerm()+" "+getRight().toString()+")";
	}
	
}
