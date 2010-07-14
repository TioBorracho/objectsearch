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
