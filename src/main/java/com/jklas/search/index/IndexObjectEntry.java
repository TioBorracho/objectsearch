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
