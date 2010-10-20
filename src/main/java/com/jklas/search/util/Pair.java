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
package com.jklas.search.util;

public class Pair<A,B> {
	
	private A first;
	
	private B second;
	
	public Pair(A a, B b) {
		setFirst(a);
		setSecond(b);
	}
	
	public A getFirst() {
		return first;
	}
	
	public B getSecond() {
		return second;
	}
	
	public void setFirst(A a) {
		this.first = a;
	}
	
	public void setSecond(B b) {
		this.second = b;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this==obj) return true;
		
		if(obj==null) return false;
		
		if(!obj.getClass().equals(getClass())) return false;
		
		Pair<?,?> other = (Pair<?,?>)obj;
		
		Object otherA = other.getFirst();
		Object otherB = other.getSecond();
		
		return otherA.equals(getFirst()) && otherB.equals(getSecond());
	}
	
	@Override
	public int hashCode() {
		return (getFirst().hashCode()+1)*(getSecond().hashCode()+1);
	}
}
 