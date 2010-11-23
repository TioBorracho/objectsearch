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

import com.jklas.search.annotations.IndexReference;
import com.jklas.search.engine.Language;
import com.jklas.search.engine.operations.StopWordProvider;
import com.jklas.search.engine.processor.ObjectTextProcessor;
import com.jklas.search.engine.stemming.StemType;
import com.jklas.search.engine.stemming.StemmerStrategy;
import com.jklas.search.indexer.Transform;

/**
 * 
 * This object contains all the information needed by the 
 * Search Engine to work with a particular entity's field.
 * 
 * @author Julián Klas
 *
 */
public class MappedFieldDescriptor {

	private Field field;
	
	private StemmerStrategy stemmer;

	private StemType stemType = StemType.NO_STEM;
	
	private Transform<?> transform;
	
	private Language language = Language.UNKOWN_LANGUAGE;
	
	private boolean searchFilter = false;
	
	private boolean searchFilterAccessByGet = false;

	private boolean accessByGet;

	private boolean isSearchField;

	private boolean sortField;

	private boolean searchSortAccessByGet;

	private ObjectTextProcessor textProcessor;

	private StopWordProvider stopWordProvider;
	
	private boolean isSearchCollection;

	private IndexReference referenceType ;

	private boolean isSearchContained;
	
	public MappedFieldDescriptor(Field field) {
		this.field = field;
	}

	public void setStemmerStrategy(StemmerStrategy stemmer) {
		this.stemmer = stemmer;
	}
	
	public StemmerStrategy getStemmerStrategy() {
		return stemmer;
	}
	
	public Field getField() {
		return field;
	}

	public void setTransformation(Transform<?> transform) {
		this.transform = transform;
	}

	public Transform<?> getTransformation() {		
		return this.transform;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Language getLanguage() {
		return this.language;
	}

	public boolean isAccessByGet() {
		return accessByGet;
	}
	
	public void setAccessByGet(boolean accessByGet) {
		this.accessByGet = accessByGet;
	}

	public void setStemType(StemType stemType) {
		this.stemType = stemType;
	}
	
	public StemType getStemType() {
		return stemType;
	}

	public void setSearchFilter(boolean indexFilter) {
		this.searchFilter = indexFilter;
	}
	
	public boolean isFilterField() {		
		return searchFilter;
	}

	public boolean isSearchFilterAccessByGet() {
		return searchFilterAccessByGet;
	}
	
	public void setIndexFilterAccessByGet(boolean indexFilterAccessByGet) {
		this.searchFilterAccessByGet = indexFilterAccessByGet;
	}
	
	public void setSearchField(boolean indexable) {
		this.isSearchField = indexable;
	}
	
	public boolean isSearchField() {
		return isSearchField;
	}

	public boolean isSortField() {		
		return this.sortField;
	}
	
	public void setSortField(boolean sortField) {
		this.sortField = sortField;
	}

	public boolean isSortFilterAccessByGet() {
		return this.searchSortAccessByGet;
	}
	
	public void setSearchSortAccessByGet(boolean searchSortAccessByGet) {
		this.searchSortAccessByGet = searchSortAccessByGet;
	}

	public ObjectTextProcessor getTextProcessor() {		
		return textProcessor;
	}
	
	public void setTextProcessor(ObjectTextProcessor textProcessor) {
		this.textProcessor = textProcessor;
	}

	public void setSearchCollection(boolean isSearchCollection) {
		this.isSearchCollection = isSearchCollection;
	}
	
	public boolean isSearchCollection() {
		return isSearchCollection;
	}

	public void setReferenceType(IndexReference referenceType) {
		this.referenceType = referenceType;		
	}
	
	public IndexReference getReferenceType() {
		return referenceType;
	}

	public void setSearchContained(boolean isSearchContained) {
		this.isSearchContained = isSearchContained;
	}

	public boolean isSearchContained() {
		return isSearchContained;
	}
	
	public StopWordProvider getStopWordProvider() {
		return stopWordProvider;
	}
	
	public void setStopWordProvider(StopWordProvider stopWordProvider) {
		this.stopWordProvider = stopWordProvider;
	}
}
