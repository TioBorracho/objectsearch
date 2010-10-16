package com.jklas.search.query.operator;

import com.jklas.search.engine.dto.ObjectResult;



public abstract class BinarySimetricOperator<E extends ObjectResult> extends BinaryOperator<E> {
	
	public BinarySimetricOperator(Operator<E> leftOperator, Operator<E> rightOperator) {
		super(leftOperator, rightOperator);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if(obj == null) return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		BinarySimetricOperator<?> other = (BinarySimetricOperator<?>) obj;
		
		return (getLeft().equals(other.getLeft()) && getRight().equals(other.getRight())) ||
			(getLeft().equals(other.getRight()) && getRight().equals(other.getLeft()));		
	}
		
}
