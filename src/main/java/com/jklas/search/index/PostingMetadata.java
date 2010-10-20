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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PostingMetadata implements Serializable {

	public static final Serializable SOURCE_FIELD_VALUE = new Serializable(){
		private static final long serialVersionUID = -194595353521830599L;
	};

	private static final long serialVersionUID = 6984200919399345753L;

	@SuppressWarnings("unchecked")
	public static final PostingMetadata NULL = new PostingMetadata(Collections.EMPTY_MAP, Collections.EMPTY_MAP);

	private Map<Field,Serializable> fieldValueMap = new HashMap<Field,Serializable>();

	private Map<Field,Integer> fieldTfMap = Collections.emptyMap();

	public void addSourceField(Field field) {
		if(!fieldValueMap.containsKey(field)) storeFieldValue(field, SOURCE_FIELD_VALUE);
	}

	public void setFieldValueMap(Map<Field,Serializable> fieldValueMap) {
		this.fieldValueMap = fieldValueMap;
	}
	
	public void storeFieldValue(Map<Field,Serializable> newFieldValueMap) {
		this.fieldValueMap.putAll(newFieldValueMap);		
	}
	
	public void storeFieldValue(Field field, Serializable value) {
		fieldValueMap.put(field, value);
	}

	public boolean isStoredField(Field field) {
		Object stored = fieldValueMap.get(field);
		return stored != null && stored!=SOURCE_FIELD_VALUE;
	}

	public boolean isSourceField(Field field) {		
		return fieldValueMap.get(field) != null;
	}

	public Object getStoredFieldValue(Field field) {		
		return fieldValueMap.get(field);
	}

	public PostingMetadata(Map<Field,Serializable> fieldValueMap, Map<Field,Integer> fieldTfMap) {			
		this.fieldValueMap = fieldValueMap;
		this.fieldTfMap = fieldTfMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
		+ ((fieldTfMap == null) ? 0 : fieldTfMap.hashCode());
		result = prime * result
		+ ((fieldValueMap == null) ? 0 : fieldValueMap.hashCode());
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
		PostingMetadata other = (PostingMetadata) obj;
		if (fieldTfMap == null) {
			if (other.fieldTfMap != null)
				return false;
		} else if (!fieldTfMap.equals(other.fieldTfMap))
			return false;
		if (fieldValueMap == null) {
			if (other.fieldValueMap != null)
				return false;
		} else if (!fieldValueMap.equals(other.fieldValueMap))
			return false;
		return true;
	}

	public PostingMetadata() {
		this( new HashMap<Field, Serializable>(8,1.0f), new HashMap<Field,Integer>(8,1.0f) );
	}

	@Override
	public String toString() {	
		StringBuilder str = new StringBuilder();

		str.append("TF: ");

		for (Entry<Field,Integer> fieldTf : fieldTfMap.entrySet()) {
			str.append("tf('");
			str.append(String.valueOf(fieldTf.getKey().getName()));
			str.append("')=");
			str.append(String.valueOf(fieldTf.getValue()));
			str.append("\n");			
		}

		for (Map.Entry<Field, Serializable> fvmEntry : fieldValueMap.entrySet()) {
			str.append(fvmEntry.getKey().getName());

			str.append(":\t");

			Object value = fvmEntry.getValue();

			if(value == SOURCE_FIELD_VALUE)
				str.append("(not stored)");
			else
				str.append(value.toString());

			str.append("\n");
		}

		return str.toString();
	}

	public Collection<Field> getStoredFields() {
		List<Field> storedFieldList = new ArrayList<Field>();

		for (Map.Entry<Field,Serializable> entry: fieldValueMap.entrySet()) {
			if(!SOURCE_FIELD_VALUE.equals(entry.getValue())) storedFieldList.add(entry.getKey());
		}

		return storedFieldList;
	}

	public Collection<Field> getSourceFields() {
		List<Field> sourceFieldList = new ArrayList<Field>();

		for (Map.Entry<Field,Serializable> entry: fieldValueMap.entrySet()) {
			if(SOURCE_FIELD_VALUE.equals(entry.getValue())) sourceFieldList.add(entry.getKey());
		}

		return sourceFieldList;
	}

	public Map<Field, Integer> getFieldTfMap() {
		return fieldTfMap;
	}

	public void merge(PostingMetadata newMetadata) {
		for (Entry<Field,Integer> fieldTf: newMetadata.getFieldTfMap().entrySet()) {
			Field field = fieldTf.getKey();
			Integer newTf = fieldTf.getValue();

			if(newTf == null) throw new IllegalArgumentException("Null term frequency, can't merge metadata for field " + fieldTf.getKey());

			Integer oldTf = fieldTfMap.get(field);

			if(oldTf == null) oldTf = 0;

			fieldTfMap.put( field , oldTf + newTf) ;
		}
	}

	public int getTfForField(Field field) {
		Integer tf = fieldTfMap.get(field);
		return tf == null ? 0 : tf;
	}

	public void addOrPutTf(Field currentField, int tfToAdd) {
		Integer currentTf = fieldTfMap.get(currentField);

		if(currentTf == null) currentTf = 0;

		fieldTfMap.put(currentField, currentTf + tfToAdd);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		// first we write the number of stored fields
		out.writeObject(fieldValueMap.size());

		// then we serialize the Field object as (Declaring Class Name ; Field Name)
		// continuing with it's value
		for (Entry<Field, Serializable> entry : fieldValueMap.entrySet()) {
			out.writeObject(entry.getKey().getDeclaringClass());
			out.writeObject(entry.getKey().getName());
			out.writeObject(entry.getValue());
		}

		// now we write the TF map size
		out.writeObject(fieldTfMap.size());

		// then we serialize the Field object as (Declaring Class Name ; Field Name)
		// continuing with it's value
		for (Entry<Field, Integer> entry : fieldTfMap.entrySet()) {
			out.writeObject(entry.getKey().getDeclaringClass().getName());
			out.writeObject(entry.getKey().getName());
			out.writeObject(entry.getValue());
		}
	}

	private void readObject(ObjectInputStream in) throws IOException {
		Integer numberOfFields;
		try {
			numberOfFields = (Integer)in.readObject();

			this.fieldValueMap = new HashMap<Field,Serializable>();

			for (int i = 0; i < numberOfFields; i++) {			
				Class<?> fieldDeclaringClass = (Class<?>)in.readObject();
				String fieldName = (String)in.readObject();
				try {
					Field field;
					field = fieldDeclaringClass.getDeclaredField(fieldName);
					Serializable value = (Serializable) in.readObject();
					this.fieldValueMap.put(field, value);
				} catch (SecurityException e) {
					throw new IOException("Couldn't get class "+fieldDeclaringClass+ " for field "+fieldName,e);
				} catch (NoSuchFieldException e) {
					throw new IOException("Couldn't get class "+fieldDeclaringClass+ " for field "+fieldName,e);
				} catch (ClassNotFoundException e) {
					throw new IOException("Couldn't get class "+fieldDeclaringClass+ " for field "+fieldName,e);
				}
			}

			numberOfFields = (Integer)in.readObject();

			this.fieldTfMap = new HashMap<Field,Integer>();
			
			for (int i = 0; i < numberOfFields; i++) {			
				String fieldDeclaringClass = (String)in.readObject();
				String fieldName = (String)in.readObject();
				try {
					Field field;
					field = Class.forName(fieldDeclaringClass).getDeclaredField(fieldName);
					Integer value = (Integer)in.readObject();
					this.fieldTfMap.put(field, value);
				} catch (SecurityException e) {
					throw new IOException("Couldn't get class "+fieldDeclaringClass+ " for field "+fieldName,e);
				} catch (NoSuchFieldException e) {
					throw new IOException("Couldn't get class "+fieldDeclaringClass+ " for field "+fieldName,e);
				} catch (ClassNotFoundException e) {
					throw new IOException("Couldn't get class "+fieldDeclaringClass+ " for field "+fieldName,e);
				}
			}
		} catch (ClassNotFoundException e1) {
			throw new IOException("Couldn't get class while reading field",e1);
		}
	}	
}
