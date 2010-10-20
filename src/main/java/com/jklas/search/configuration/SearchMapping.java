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
package com.jklas.search.configuration;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jklas.search.engine.Language;
import com.jklas.search.engine.processor.ObjectTextProcessor;
import com.jklas.search.exception.SearchEngineMappingException;
import com.jklas.search.index.selector.IndexSelector;

public class SearchMapping {

	/**
	 * Holds the correspondence between mapped fields and their
	 * descriptors.
	 */
	private Map<Field,MappedFieldDescriptor> mappedFields = new ConcurrentHashMap<Field,MappedFieldDescriptor>();

	/**
	 * Strategy for index selection
	 */
	private IndexSelector indexSelector;
	
	/**
	 * Field that holds the object's identifier
	 */
	private Field idField;
	
	/**
	 * The language that might be specified statically
	 * for all objects mapped thru this mapping
	 */
	private Language language;
	
	private Field languageSelectorField;

	private boolean isLanguageSelectedByField;

	private ObjectTextProcessor textProcessor;

	private boolean isIndexableContainer =false;

	private boolean isIndexable;
	
	/**
	 * Adds a new field to this mapping.
	 * 
	 * If the field is already mapped, it is replaced.
	 * 
	 * @param field the field being mapped
	 * @param fieldDescriptor
	 * @return this mapping, which might be used for concatenation
	 */
	public SearchMapping put(Field field, MappedFieldDescriptor fieldDescriptor) {
		mappedFields.put(field,fieldDescriptor);
		return this;
	}
	
	/**
	 * Tells if a field is mapped under this mapping.
	 * 
	 * @param field the field that might be mapped
	 * @return true if the field is mapped, false otherwise
	 */
	public boolean isMapped(Field field) {		
		return mappedFields.containsKey(field);
	}

	/**
	 * Sets the strategy for index selection under this mapping 
	 * 
	 * @param indexSelector the class that knows how to select the index
	 */
	public void setIndexSelector(IndexSelector indexSelector) {
		this.indexSelector = indexSelector;
	}
	
	/**
	 * Returns the current index selector for this mapping
	 * 
	 * @return the index selector 
	 */
	public IndexSelector getIndexSelector() {
		return indexSelector;
	}

	/**
	 * 
	 * Returns the descriptor for the specified field.
	 * 
	 * @param mappedField the described field
	 * @return the descriptor
	 */
	public MappedFieldDescriptor getFieldDescriptor(Field mappedField) {
		return mappedFields.get(mappedField);
	}

	public void put(Class<?> clazz, String fieldName, MappedFieldDescriptor fieldDescriptor) throws SecurityException, NoSuchFieldException {
		put(clazz.getDeclaredField(fieldName), fieldDescriptor);
	}
	
	public void put(Object object, String fieldName, MappedFieldDescriptor fieldDescriptor) throws SecurityException, NoSuchFieldException {
		put(object.getClass().getDeclaredField(fieldName), fieldDescriptor);
	}

	/**
	 * Sets the field that is holding
	 * the object unique identifier.
	 * 
	 * The field value must serializable.
	 * 
	 * @param field the field that holds the object id
	 */
	public void setIdField(Field field) {
		this.idField = field;
	}

	/**
	 * Returns the field that holds the identifier for
	 * the objects being mapped under this mapping
	 * 
	 * @return the field that holds the object id
	 */
	public Field getIdField() {
		return idField;
	}
	
	public void setLanguage(Language language) {
		setLanguageSelectedByField(false);
		this.language = language;
	}
	
	public Language getLanguage() {
		return language;
	}
	
	public Field getLanguageSelectorField() {
		return languageSelectorField;
	}
	
	public void setLanguageSelectorField(Field languageSelectorField) {
		setLanguageSelectedByField(true);
		this.languageSelectorField = languageSelectorField;
	}
	
	public boolean isLanguageSelectedByField() {
		return isLanguageSelectedByField;
	}
	
	private void setLanguageSelectedByField(boolean isLanguageSelectedByField) {
		this.isLanguageSelectedByField = isLanguageSelectedByField;
	}
	
	public Map<Field, MappedFieldDescriptor> getMappedFields() {
		return mappedFields;
	}

	public Object extractId(Object entity) throws SearchEngineMappingException {
		Field idField = getIdField();
		
		if(idField == null) throw new SearchEngineMappingException("Can't map objects without id: "+entity);
		
		idField.setAccessible(true);
		try {
			return idField.get(entity);
		} catch (IllegalArgumentException e) {
			throw new SearchEngineMappingException("Could not access id field",e);
		} catch (IllegalAccessException e) {			
			throw new SearchEngineMappingException("Could not access id field",e);
		}				
	}

	public ObjectTextProcessor getTextProcessor() {		
		return textProcessor;
	}
	
	public void setTextProcessor(ObjectTextProcessor textProcessor) {
		this.textProcessor = textProcessor;
	}

	public void setIndexableContainer(boolean isIndexContainer) {
		this.isIndexableContainer = isIndexContainer;
	}
	
	public boolean isIndexableContainer() {
		return isIndexableContainer;
	}

	public void setIndexable(boolean isIndexable) {
		this.isIndexable = isIndexable; 
	}
	
	public boolean isIndexable() {
		return isIndexable;
	}
}
