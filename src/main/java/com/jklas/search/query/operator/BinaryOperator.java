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
