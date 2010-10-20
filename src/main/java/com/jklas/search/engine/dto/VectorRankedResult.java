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
package com.jklas.search.engine.dto;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import com.jklas.search.index.ObjectKey;

public class VectorRankedResult extends ObjectResult implements Comparable<VectorRankedResult> {

	private double score;
		
	@SuppressWarnings("unchecked")
	public VectorRankedResult(ObjectKey posting, double score) {
		this(posting, score, Collections.EMPTY_MAP);
	}
	
	public VectorRankedResult(ObjectKey posting, Map<Field, Object> storedFields) {
		super(posting, storedFields);	
		this.score = 0.0d;
	}
	
	public VectorRankedResult(ObjectKey posting, double score, Map<Field, Object> storedFields) {
		super(posting, storedFields);	
		this.score = score;
	}
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		ObjectKey posting = getKey();
		result = prime * result + ((posting== null) ? 0 : posting.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VectorRankedResult other = (VectorRankedResult) obj;
		ObjectKey posting = getKey();
		if (posting == null) {
			if (other.getKey() != null)
				return false;
		} else if (!posting.equals(other.getKey()))
			return false;
		return true;
	}

	public double getScore() {
		return score;
	}
	
	@Override
	public int compareTo(VectorRankedResult o) {
		int result = -Double.compare(score, o.score);
		
		if(result != 0) return result;
		else {
			result = getKey().getClazz().getName().compareTo(o.getKey().getClazz().getName());
			if(result != 0) return result;
			else {
				return getKey().getId().toString().compareTo(o.getKey().getId().toString());
			}
		}
	}
	
	public void addScore(double score) {
		this.score += score;
	}
}
