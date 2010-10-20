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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.jklas.search.SearchEngine;
import com.jklas.search.annotations.IndexSelector;
import com.jklas.search.annotations.Indexable;
import com.jklas.search.annotations.IndexableContainer;
import com.jklas.search.annotations.LangId;
import com.jklas.search.annotations.LangSelector;
import com.jklas.search.annotations.NotIndexable;
import com.jklas.search.annotations.SearchCollection;
import com.jklas.search.annotations.SearchContained;
import com.jklas.search.annotations.SearchField;
import com.jklas.search.annotations.SearchFilter;
import com.jklas.search.annotations.SearchId;
import com.jklas.search.annotations.SearchSort;
import com.jklas.search.annotations.Stemming;
import com.jklas.search.annotations.TextProcessor;
import com.jklas.search.engine.Language;
import com.jklas.search.engine.processor.ObjectTextProcessor;
import com.jklas.search.engine.stemming.StemmerStrategy;
import com.jklas.search.exception.SearchEngineMappingException;
import com.jklas.search.index.selector.IndexSelectorByConstant;
import com.jklas.search.index.selector.IndexSelectorByField;
import com.jklas.search.indexer.Transform;


public class AnnotationConfigurationMapper {

	private final boolean recursive ;
	
	public AnnotationConfigurationMapper(boolean recursive) {
		this.recursive = recursive;
	}
	
	public AnnotationConfigurationMapper() {
		this(false);
	}

	/**
	 * Maps a single entity and configures the Search Engine
	 * if it's not already configured.
	 * 
	 * If the entity being mapped is an @IndexableContainer,
	 * you can use the recursive parameter to map the contained
	 * objects.
	 * 
	 * @param entity the entity being mapped
	 * @param recursive true if you want to map underlying contained entities, false otherwise 
	 * @throws SearchEngineMappingException in case something goes wrong
	 */
	public static void configureAndMap(Object entity, boolean recursive) throws SearchEngineMappingException {
		SearchEngine search = SearchEngine.getInstance();

		if(!search.isConfigured()) search.newConfiguration();

		AnnotationConfigurationMapper acm = new AnnotationConfigurationMapper(recursive);

		acm.map(entity);
	}

	/**
	 * Maps a single entity and configures the Search Engine
	 * if it's not already configured.
	 * 
	 * If the entity being mapped is an @IndexableContainer,
	 * you must map the @SearchContained entity separately.
	 * 
	 * @param entity the entity being mapped
	 * @throws SearchEngineMappingException in case something goes wrong
	 */
	public static void configureAndMap(Object entity) throws SearchEngineMappingException {
		configureAndMap(entity,false);
	}

	public boolean isOrContainsIndexable(Object entity) {
		Class<?> clazz = entity.getClass();

		return isIndexable(clazz) || isIndexableContainer(clazz);
	}

	public boolean isIndexable(Class<?> clazz) {
		return clazz.getAnnotation(Indexable.class)!=null;
	}

	public boolean isIndexable(Object entity) {
		return entity.getClass().getAnnotation(Indexable.class)!=null;
	}

	public boolean isIndexableContainer(Class<?> clazz) {	
		return clazz.getAnnotation(IndexableContainer.class)!=null;
	}

	/**
	 * Maps an entity using current global configuration.
	 * @throws SearchEngineMappingException 
	 * 
	 * @see AnnotationConfigurationMapper#map(Object, SearchConfiguration) 
	 */
	public boolean map(Class<?> clazz) throws SearchEngineMappingException {
		return map(clazz,SearchEngine.getInstance().getConfiguration());
	}

	/**
	 * Maps an entity using current global configuration.
	 * @throws SearchEngineMappingException 
	 * 
	 * @see AnnotationConfigurationMapper#map(Object, SearchConfiguration) 
	 */
	public boolean map(Object entity) throws SearchEngineMappingException {
		return map(entity.getClass(),SearchEngine.getInstance().getConfiguration());
	}

	public synchronized boolean map(Object entity, SearchConfiguration configuration) throws SearchEngineMappingException  {
		if(entity==null) throw new IllegalArgumentException("You can't map a null entity"); 
		return map(entity.getClass(), configuration);
	}


	/**
	 * Maps an entity using a specific configuration.
	 * 
	 * If the entity is already mapped it returns false
	 * without remapping it.
	 * 
	 * @param entity the entity to be mapped
	 * @return true if the entity was correctly mapped, false otherwise 
	 * @throws SearchEngineMappingException 
	 * @throws SearchEngineMappingException when a field declaration can't be set up into the configuration 
	 */
	public synchronized boolean map(Class<?> clazz, SearchConfiguration configuration) throws SearchEngineMappingException  {

		if(configuration==null) throw new IllegalArgumentException("You can't map into a null configuration");

		if(configuration.isMapped(clazz)) return false;

		boolean notIndexableButContainer = false;

		Class<?> superClassForCascading = null, climbTarget = null;

		// if the class is explicitly not indexable, we don't
		// have much here to do...mapClass(superClassForCascading,mapping)
		if(isExplicitNotIndexable(clazz)) {
			SearchMapping mapping = configuration.addEmptyMapping(clazz);
			mapping.setIndexable(false);
			return true;
		}
		
		// if the class is not indexable itself, it may still be indexable
		// by inheritance or might be an IndexableContainer
		if(!isIndexable(clazz)) {
			superClassForCascading = getIndexableSuperClassFor(clazz); 			
			if(superClassForCascading == null) {				
				if(isIndexableContainer(clazz))	notIndexableButContainer = true;
				else return false;
			}
		}

		boolean hasSuperClassForCascading = (superClassForCascading != null);

		if(hasSuperClassForCascading) {
			climbTarget = getClimbTargetFor(superClassForCascading);			
		} else {
			climbTarget = getClimbTargetFor(clazz);
		}		

		SearchMapping mapping = configuration.addEmptyMapping(clazz);

		try {
			if(notIndexableButContainer) {
				mapPureContainerClass(clazz,mapping);
				mapContainerFields(clazz,mapping);
			} else {
				mapContainerFields(clazz,mapping);
				if(hasSuperClassForCascading) mapClass(superClassForCascading,mapping);

				mapClass(clazz,mapping);

				if(superClassForCascading != null)	mapFields(clazz,superClassForCascading,climbTarget,mapping);
				else mapFields(clazz,clazz,climbTarget,mapping);				
			}			
		} catch (SearchEngineMappingException e) {
			configuration.removeMap(clazz);
			throw e;
		}

		return true;
	}


	private boolean isExplicitNotIndexable(Class<?> clazz) {
		return clazz.getAnnotation(NotIndexable.class)!=null;
	}

	private void mapPureContainerClass(Class<?> clazz, SearchMapping mapping) {
		mapping.setIndexable(false);
		mapping.setIndexableContainer(true);
	}

	private void mapContainerFields(Class<?> clazz, SearchMapping mapping) throws SearchEngineMappingException {
		Field[] fields = clazz.getDeclaredFields();

		boolean currentFieldIsCollectionOrContained ;
		for (int i = 0; i < fields.length; i++) 
		{
			Field currentField = fields[i];

			currentField.setAccessible(true);
			
			currentFieldIsCollectionOrContained = false;
			
			// 1st check: collections
			SearchCollection collectionAnnotation = currentField.getAnnotation(SearchCollection.class);
			if(collectionAnnotation!=null) 	{
				mapCollectionField(mapping, currentField, collectionAnnotation);
				currentFieldIsCollectionOrContained = true;
			} else {
				// 2nd check: contained elements that are fully mappeable
				SearchContained containedAnnotation = currentField.getAnnotation(SearchContained.class);
				if(containedAnnotation!=null) 	{
					mapContainedField(mapping, currentField, containedAnnotation);
					currentFieldIsCollectionOrContained = true;
				}
			}
			
			if(recursive && currentFieldIsCollectionOrContained) {
				map(currentField.getType());
			}
		}		
	}

	private void mapCollectionField(SearchMapping mapping, Field currentField, SearchCollection searchCollectionAnnotation) throws SearchEngineMappingException {
		try {				
			// We must allow the object itself to be null since if may be a collection
			if(Iterable.class.isAssignableFrom(currentField.getType())) {
				MappedFieldDescriptor fieldDescriptor = new MappedFieldDescriptor(currentField);
				fieldDescriptor.setSearchCollection(true);
				fieldDescriptor.setSearchField(false);
				fieldDescriptor.setReferenceType(searchCollectionAnnotation.reference());
				mapping.put(currentField, fieldDescriptor);						
			}
		} catch (IllegalArgumentException e) {
			throw new SearchEngineMappingException("Couldn't access collection field " + currentField);
		}
	}

	private void mapContainedField(SearchMapping mapping, Field currentField, SearchContained containedAnnotation) throws SearchEngineMappingException {
		try {			
			MappedFieldDescriptor fieldDescriptor = new MappedFieldDescriptor(currentField);
			
			fieldDescriptor.setSearchCollection(false);
			fieldDescriptor.setSearchField(false);
			fieldDescriptor.setSearchContained(true);
			fieldDescriptor.setReferenceType(containedAnnotation.reference());
			mapping.put(currentField, fieldDescriptor);				
		} catch (IllegalArgumentException e) {
			throw new SearchEngineMappingException("Couldn't access collection field " + currentField);
		}
	}

	private void mapFields(Class<?> baseClass, Class<?> superClassForCascading, Class<?> climbTargetClass ,SearchMapping mapping) throws SearchEngineMappingException {

		ArrayList<Field> fields = new ArrayList<Field>();

		Class<?> currentClass = baseClass;

		boolean noFurtherFields = false;

		boolean climbing = (climbTargetClass != null);

		// fetchs attributes from hierarchy, starts with childs and ends on superclass
		while(!noFurtherFields) {
			noFurtherFields = currentClass.equals(Object.class) ||
			( !climbing && superClassForCascading.equals(currentClass)) ||
			( climbing && climbTargetClass.equals(currentClass));
			fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
			currentClass = currentClass.getSuperclass();
		}

		// To override superclass mappings, the first fields to be processed
		// must be the superclass ones, following the child's fields 
		Collections.reverse(fields);

		mapContainerFields(baseClass, mapping);

		for (Field currentField : fields) {

			if(fieldMustBeProcessed(currentField)) {
				MappedFieldDescriptor fieldDescriptor = new MappedFieldDescriptor(currentField);

				checkField(currentField, baseClass, mapping);

				if(isSearchField(currentField)) {
					fieldDescriptor.setSearchField(true);
					mapSearchField(currentField,baseClass,mapping,fieldDescriptor);					
				} else {
					fieldDescriptor.setSearchField(false);
				}

				if(isSearchFilter(currentField)) setSearchFilter(currentField,baseClass,fieldDescriptor);

				if(isSearchSort(currentField)) setSearchSort(currentField,baseClass,fieldDescriptor);

				mapping.put(currentField, fieldDescriptor);
			}

		}
	}

	private Class<?> getIndexableSuperClassFor(Class<?> clazz) {
		while(true) {
			Class<?> superClass = clazz.getSuperclass();

			if(superClass == null) return null;

			if(isIndexable(superClass)) {
				Indexable superClassIndexableAnnotation = superClass.getAnnotation(Indexable.class);
				if(superClassIndexableAnnotation.makeSubclassesIndexable()) return superClass;
				else return null;
			} else {
				clazz = superClass;
			}
		}
	}

	private Class<?> getClimbTargetFor(Class<?> clazz) {

		Indexable indexableAnnotation = clazz.getAnnotation(Indexable.class);

		if(indexableAnnotation == null) return null;

		Class<?> climbTargetClass = indexableAnnotation.climbingTarget();

		if(Indexable.NoClimbing.class.equals(climbTargetClass) || climbTargetClass == null) return null;

		return climbTargetClass;
	}	

	private void mapClass(Class<?> clazz, SearchMapping mapping) throws SearchEngineMappingException {
		Indexable indexableAnnotation = clazz.getAnnotation(Indexable.class);

		if(indexableAnnotation!=null) mapping.setIndexSelector(new IndexSelectorByConstant(indexableAnnotation.indexName()));

		TextProcessor textProcessorAnnotation = clazz.getAnnotation(TextProcessor.class);

		if(textProcessorAnnotation!=null) mapping.setTextProcessor( (ObjectTextProcessor) findNoArgConstructorAndInvoke(textProcessorAnnotation.value()) );
		else if(mapping.getTextProcessor()==null) mapping.setTextProcessor( (ObjectTextProcessor) findNoArgConstructorAndInvoke(ObjectTextProcessor.DFLT_TEXT_PROCESSOR) );

		LangId langAnnotation = clazz.getAnnotation(LangId.class);

		if(langAnnotation!=null) mapping.setLanguage(new Language(langAnnotation.value()));

		mapping.setIndexableContainer(isIndexableContainer(clazz));
		mapping.setIndexable(true);
	}

	private void setSearchSort(Field currentField, Class<?> clazz, MappedFieldDescriptor fieldDescriptor) {
		SearchSort sortAnnotation = currentField.getAnnotation(SearchSort.class);

		if(sortAnnotation!=null) {
			fieldDescriptor.setSortField(true);			
			fieldDescriptor.setSearchSortAccessByGet(sortAnnotation.accessByGet());
		} else {
			fieldDescriptor.setSortField(false);
		}
	}

	private boolean isSearchSort(Field currentField) {
		return currentField.getAnnotation(SearchSort.class)!=null;
	}

	private boolean isSearchFilter(Field currentField) {
		return currentField.getAnnotation(SearchFilter.class)!=null;
	}

	private boolean isSearchField(Field field) {		
		return field.getAnnotation(SearchField.class)!=null;
	}

	private void checkField(Field field, Class<?> clazz, SearchMapping mapping) {
		checkSearchId(field,mapping);
		checkFieldLanguageSelector(field,clazz,mapping);
		checkFieldIndexSelector(field,mapping);
	}

	private boolean fieldMustBeProcessed(Field field) {
		return field.getAnnotation(SearchField.class)!=null ||
		field.getAnnotation(SearchSort.class)!=null ||
		field.getAnnotation(SearchFilter.class)!=null ||		
		field.getAnnotation(SearchId.class)!=null ||
		field.getAnnotation(LangSelector.class)!=null ||
		field.getAnnotation(IndexSelector.class)!=null;
	}

	private void mapSearchField(Field field, Class<?> clazz, SearchMapping mapping, MappedFieldDescriptor fieldDescriptor) throws SearchEngineMappingException {		
		setSearchField(field,clazz,mapping,fieldDescriptor);
		setTextProcessor(field,clazz,mapping,fieldDescriptor);
		setStemmingStrategy(field,clazz,fieldDescriptor);	
	}

	private void setTextProcessor(Field field, Class<?> clazz, SearchMapping mapping, MappedFieldDescriptor fieldDescriptor) throws SearchEngineMappingException {
		TextProcessor textProcessorAnnotation = field.getAnnotation(TextProcessor.class);

		ObjectTextProcessor fieldTextProcessor;

		if(textProcessorAnnotation!=null)
			fieldTextProcessor = (ObjectTextProcessor)findNoArgConstructorAndInvoke(textProcessorAnnotation.value());
		else
			fieldTextProcessor = mapping.getTextProcessor();

		fieldDescriptor.setTextProcessor(fieldTextProcessor);
	}

	private void setSearchFilter(Field field, Class<?> clazz, MappedFieldDescriptor fieldDescriptor) {
		SearchFilter indexFilterAnnotation = field.getAnnotation(SearchFilter.class);

		if(indexFilterAnnotation!=null) {
			fieldDescriptor.setSearchFilter(true);			
			fieldDescriptor.setIndexFilterAccessByGet(indexFilterAnnotation.accessByGet());
		} else {
			fieldDescriptor.setSearchFilter(false);
		}
	}

	private void checkFieldIndexSelector(Field field, SearchMapping mapping) {
		IndexSelector selectorAnnotation = field.getAnnotation(IndexSelector.class);

		if(selectorAnnotation!=null) mapping.setIndexSelector(new IndexSelectorByField(field));
	}

	private void checkSearchId(Field field, SearchMapping mapping) {
		SearchId idAnnotation = field.getAnnotation(SearchId.class);

		if(idAnnotation!=null) mapping.setIdField(field);
	}

	private void checkFieldLanguageSelector(Field field, Class<?> clazz, SearchMapping mapping) {
		LangSelector langSelector = field.getAnnotation(LangSelector .class);

		if(langSelector!=null )	mapping.setLanguageSelectorField(field);
	}

	private void setSearchField(Field field, Class<?> clazz, SearchMapping classMapping, MappedFieldDescriptor fieldDescriptor) throws SearchEngineMappingException {
		SearchField searchFieldAnnotation = field.getAnnotation(SearchField.class);

		if(searchFieldAnnotation!=null) {
			fieldDescriptor.setAccessByGet(searchFieldAnnotation.accessByGet());
			fieldDescriptor.setTransformation((Transform<?>) findNoArgConstructorAndInvoke(searchFieldAnnotation.transform()));			
		}		
	}

	private Object findNoArgConstructorAndInvoke(Class<?> clazz) throws SearchEngineMappingException {
		try {
			Constructor<?>[] constructors = clazz.getDeclaredConstructors();

			for (int i = 0; i < constructors.length; i++) {
				if(constructors[i].getParameterTypes().length==0) {
					constructors[i].setAccessible(true);						
					return constructors[i].newInstance(new Object[]{});
				}
			}

			throw new SearchEngineMappingException("Couldn't find a no-arg constructor for class " + clazz);
		} catch (IllegalArgumentException e) {
			throw new SearchEngineMappingException(clazz+" class constructor threw an exception",e);
		} catch (InvocationTargetException e) {
			throw new SearchEngineMappingException(clazz+" class constructor threw an exception ",e);
		} catch (InstantiationException e) {
			throw new SearchEngineMappingException("Can't instantiate transformation of class "+clazz,e);
		} catch (IllegalAccessException e) {
			throw new SearchEngineMappingException("Can't access transformation "+clazz,e);
		}			
	}

	private void setStemmingStrategy(Field field, Class<?> clazz, MappedFieldDescriptor fieldDescriptor) throws SearchEngineMappingException {
		Stemming stemmingStrategyAnnotation = field.getAnnotation(Stemming.class);

		if(stemmingStrategyAnnotation!=null) {
			Class<? extends StemmerStrategy> stemmerStrategyClass = stemmingStrategyAnnotation.strategy();

			try {
				fieldDescriptor.setStemmerStrategy(stemmerStrategyClass.newInstance());				
				fieldDescriptor.setStemType(stemmingStrategyAnnotation.stemType());
			} catch (InstantiationException e) {
				throw new SearchEngineMappingException("Can't instantiate stemmer strategy of class "+stemmerStrategyClass,e);
			} catch (IllegalAccessException e) {
				throw new SearchEngineMappingException("Can't access stemmer strategy"+stemmerStrategyClass,e);
			}			
		}

	}
}
