package com.jklas.search.index;

import java.io.Serializable;

/**
 * 
 * Esta clase identifica unívocamente a un objeto
 * indexado en una posting list.
 * 
 * @author Julián Klas
 * @date 2009-07-26
 */
public class IndexObjectEntry {
	private String token;
	private String className;
	private Serializable id;
	private String fieldName;
	private String fieldType;
	private float score;
	
	public IndexObjectEntry() {		
	}
	
	public IndexObjectEntry(String token, Serializable id, String className, String fieldName, String fieldType) {
		setToken(token);
		setId(id);
		setClassName(className);
		setFieldName(fieldName);
		setFieldType(fieldType);
	}

	public IndexObjectEntry(String token, Serializable id, String className, String fieldName, String fieldType, float score) {
		setToken(token);
		setId(id);
		setClassName(className);
		setFieldName(fieldName);
		setFieldType(fieldType);
		setScore(score);
	}
	
	public void setId(Serializable id) {
		this.id = id;
	}
	public Serializable getId() {
		return id;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!obj.getClass().equals(getClass())) return false;
		
		IndexObjectEntry other =(IndexObjectEntry)obj; 
		
		return token.equals(other.token) && className.equals(other.className) &&
				fieldName.equals(other.fieldName) && id.equals(other.id) && fieldType.equals(other.fieldType);
	}
	
	@Override
	public int hashCode() {	
		return token.hashCode()*className.hashCode()*id.hashCode();
	}

	public void setScore(float score) {
		this.score = score;
	}

	public float getScore() {
		return score;
	}

}
