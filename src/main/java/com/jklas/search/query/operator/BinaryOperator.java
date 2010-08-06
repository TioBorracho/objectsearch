package com.jklas.search.query.operator;

import com.jklas.search.engine.dto.ObjectResult;



public abstract class BinaryOperator<E extends ObjectResult> extends Operator<E> {
	
	private final Operator<E> leftOperator;
	
	private final Operator<E> rightOperator;
	
	public BinaryOperator(Operator<E> leftOperator, Operator<E> rightOperator) {
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
		
		BinaryOperator<?> other = (BinaryOperator<?>) obj;
		
		return leftOperator.equals(other.leftOperator) && rightOperator.equals(other.rightOperator);
	}
	
	@Override
	public String toString() {	
		return "("+getLeft().toString()+" "+getOperatorTerm()+" "+getRight().toString()+")";
	}
	
}
