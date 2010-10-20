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
package com.jklas.search.indexer.pipeline;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jklas.search.SearchEngine;
import com.jklas.search.annotations.IndexReference;
import com.jklas.search.configuration.MappedFieldDescriptor;
import com.jklas.search.configuration.SearchConfiguration;
import com.jklas.search.configuration.SearchMapping;
import com.jklas.search.engine.Language;
import com.jklas.search.engine.processor.ObjectTextProcessor;
import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.exception.SearchEngineException;
import com.jklas.search.index.PostingMetadata;
import com.jklas.search.index.Term;
import com.jklas.search.index.dto.IndexObjectDto;
import com.jklas.search.indexer.Transform;
import com.jklas.search.util.Pair;
import com.jklas.search.util.TextLibrary;

/**
 * Implementa la inteligencia necesaria para convertir un 
 * objeto indexable en términos de un índice invertido  
 *
 */
public class DefaultIndexingPipeline implements IndexingPipeline {

	private static final SearchEngine search = SearchEngine.getInstance();

	private SearchConfiguration configuration = null;

	public SemiIndex processObject(Object entity) throws IndexObjectException {
		return processObject(entity, new HashSet<Object>());
	}
	
	@SuppressWarnings("unchecked")
	public SemiIndex processObject(Object entity, Set<Object> indexContext) throws IndexObjectException {
		if(indexContext.contains(entity)) return new SemiIndex();
		
		indexContext.add(entity);
		
		checkForConfigurationChange();

		SemiIndex semiIndex;

		boolean isIndexable = isIndexable(entity);
		boolean isIndexableContainer = isIndexableContainer(entity);

		if(isIndexable) {
			IndexObjectDto indexObjectDto = new IndexObjectDto(entity);
			semiIndex = new SemiIndex(indexObjectDto, processLeafObject(indexObjectDto));				

			if(isIndexableContainer) {
				semiIndex.merge( processContainerObject(entity, indexContext) );
			}

		} else {			
			if(isIndexableContainer) {
				semiIndex =  processContainerObject(entity, indexContext);
			} else {
				IndexObjectDto indexObjectDto = new IndexObjectDto(entity, IndexObjectDto.NO_ID);
				semiIndex = new SemiIndex(indexObjectDto, Collections.EMPTY_MAP);				
			}
		}

		return semiIndex;
	}


	private SemiIndex processContainerObject(Object entity, Set<Object> indexContext) throws IndexObjectException {
		SemiIndex semiIndex = new SemiIndex();

		SearchMapping mapping = configuration.getMapping(entity);

		List<Pair<Iterable<?>, IndexReference>> indexableCollections = getIndexableCollections(entity, mapping);

		for (Pair<Iterable<?>, IndexReference> iterable : indexableCollections) {
			for (Object indexContained : iterable.getFirst()) {
				IndexReference reference = iterable.getSecond();
				processContainedObject(entity, semiIndex, indexContained, reference, indexContext);
			}
		}

		List<Pair<Object, IndexReference>> indexableContained = getContainedObjects(entity, mapping);

		for (Pair<Object, IndexReference> contained : indexableContained) {
			IndexReference reference = contained.getSecond();
			processContainedObject(entity, semiIndex, contained.getFirst(), reference, indexContext);			
		}

		
		return semiIndex;
	}

	private void processContainedObject(Object entity, SemiIndex semiIndex, Object indexContained, IndexReference reference, Set<Object> indexContext) throws IndexObjectException {
		SemiIndex index = processObject(indexContained, indexContext);
		switch(reference) {
		
			case CONTAINER:		index.setReferences( entity );
								semiIndex.merge( index );
								break;

			case SELF: 			semiIndex.merge( index );
								break;

			case BOTH: 			SemiIndex copyWithChangedReferences = new SemiIndex(index);
								copyWithChangedReferences.setReferences( entity );
								semiIndex.merge( copyWithChangedReferences );
								semiIndex.merge( index );
								break;
		}
	}	

	private List<Pair<Object, IndexReference>> getContainedObjects(Object entity, SearchMapping mapping) throws IndexObjectException {
		List<Pair<Object, IndexReference>>  containedObjects = new ArrayList<Pair<Object, IndexReference>>();

		Map<Field, MappedFieldDescriptor> fieldMappings = mapping.getMappedFields();

		for (Map.Entry<Field, MappedFieldDescriptor> fieldMapping : fieldMappings.entrySet()) {
			if(fieldMapping.getValue().isSearchContained()) {
								
				Field containedField = fieldMapping.getKey();

				containedField.setAccessible(true);
				Object contained;
				try {
					contained = containedField.get(entity);
					if(contained == null) continue;
				} catch (IllegalArgumentException e) {
					throw new IndexObjectException("Could not access collection field "+containedField,e);
				} catch (IllegalAccessException e) {
					throw new IndexObjectException("Could not access collection field "+containedField,e);
				}

				Pair<Object,IndexReference> pair =
					new Pair<Object,IndexReference>(contained, fieldMapping.getValue().getReferenceType());  

				containedObjects.add(pair);
			}
		}

		return containedObjects;
	}

	private List<Pair<Iterable<?>, IndexReference>>  getIndexableCollections(Object entity, SearchMapping mapping) throws IndexObjectException {
		List<Pair<Iterable<?>, IndexReference>>  collections = new ArrayList<Pair<Iterable<?>, IndexReference>> ();

		Map<Field, MappedFieldDescriptor> fieldMappings = mapping.getMappedFields();

		for (Map.Entry<Field, MappedFieldDescriptor> fieldMapping : fieldMappings.entrySet()) {
			if(fieldMapping.getValue().isSearchCollection()) {
				Field collectionField = fieldMapping.getKey();

				collectionField.setAccessible(true);
				Object iterable;
				try {
					iterable = collectionField.get(entity);
					if(iterable == null) continue;
				} catch (IllegalArgumentException e) {
					throw new IndexObjectException("Could not access collection field "+collectionField,e);
				} catch (IllegalAccessException e) {
					throw new IndexObjectException("Could not access collection field "+collectionField,e);
				}

				Pair<Iterable<?>,IndexReference> pair =
					new Pair<Iterable<?>,IndexReference>((Iterable<?>)iterable, fieldMapping.getValue().getReferenceType());  

				collections.add(pair);
			}
		}

		return collections;
	}

	private boolean isIndexableContainer(Object entity) {
		if(!configuration.isMapped(entity)) return false;

		return configuration.getMapping(entity).isIndexableContainer();
	}
	
	private Map<Term, PostingMetadata> processLeafObject(IndexObjectDto indexObjectDto) throws IndexObjectException {
		Object entity = indexObjectDto.getEntity();

		Class<?> entityClass = entity.getClass();

		SearchMapping mapping = configuration.getMapping(entityClass);

		try {
			indexObjectDto.setIndexId(mapping.getIndexSelector().selectIndex(entity));
		} catch (SearchEngineException e) {
			throw new IndexObjectException("Couldn't get the index id for entity: "+entity,e);
		}

		Map<Field,MappedFieldDescriptor> indexableOrStoredFields = mapping.getMappedFields();
		Map<Field,Serializable> storedFieldValues = new HashMap<Field,Serializable>();

		Map<Term, PostingMetadata> semiIndex = new HashMap<Term, PostingMetadata>(); 

		for (Map.Entry<Field, MappedFieldDescriptor> mappedFieldEntry : indexableOrStoredFields.entrySet()) {
			Field field = mappedFieldEntry.getKey();
			MappedFieldDescriptor fieldDescriptor = mappedFieldEntry.getValue();

			if(isStorageField(fieldDescriptor)) {
				storedFieldValues.put(field, (Serializable)extractObjectInField(entity, fieldDescriptor));
			}
		}

		for (Map.Entry<Field, MappedFieldDescriptor> mappedFieldEntry : indexableOrStoredFields.entrySet()) {
			Field field = mappedFieldEntry.getKey();
			MappedFieldDescriptor fieldDescriptor = mappedFieldEntry.getValue();

			if(fieldDescriptor.isSearchField()) {
				List<Term> fieldPostings = processField(entity, field, fieldDescriptor);				
				mergeTokens( entity, field, fieldPostings, semiIndex , storedFieldValues);
			}	
		}

		return semiIndex;
	}

	/**
	 * Merges the semi index that's getting constructed for this object with
	 * the semi index generated for the current field.
	 * 
	 * @param semiIndex semi-index for the object
	 * @param currentField field that is being processed
	 * @param fieldPostings postings generated by this field analysis
	 * @param storedFieldValues 
	 * @throws IndexObjectException this exception is thrown when accesing current field value.
	 */
	private void mergeTokens(Object entity, Field currentField, List<Term> fieldPostings, Map<Term, PostingMetadata> semiIndex, Map<Field, Serializable> storedFieldValues) throws IndexObjectException {
		for (Term term : fieldPostings) {
			PostingMetadata postingMetadata = semiIndex.get(term);

			if(postingMetadata == null) {
				postingMetadata = new PostingMetadata();
				semiIndex.put(term, postingMetadata);

				postingMetadata.storeFieldValue(storedFieldValues);
			}

			postingMetadata.addSourceField(currentField);
			postingMetadata.addOrPutTf(currentField, 1);
		}
	}

	private boolean isStorageField(MappedFieldDescriptor fieldDescriptor) {
		return fieldDescriptor.isFilterField() || fieldDescriptor.isSortField();
	}

	private final boolean isIndexable(Object entity) {
		if(!configuration.isMapped(entity)) return false;

		if(!configuration.getMapping(entity).isIndexable()) return false;

		Field idField = configuration.getMapping(entity).getIdField();

		if(idField == null) return false;

		return true;
	}

	private List<Term> processField(Object entity, Field currentField, MappedFieldDescriptor fieldDescriptor) throws IndexObjectException {

		Object objectInField = extractObjectInField(entity, fieldDescriptor);

		if(objectInField == null) return Collections.emptyList();

		Object transformed = applyTransform(fieldDescriptor, objectInField);

		String text = transformed.toString();

		if(text.length() == 0) return Collections.emptyList();

		ObjectTextProcessor textProcessor = fieldDescriptor.getTextProcessor(); 

		List<Term> fieldTerms = textProcessor.processField(text, fieldDescriptor);

		return fieldTerms;
	}	

	private Object extractObjectInField(Object entity, MappedFieldDescriptor fieldDescriptor) throws IndexObjectException {
		Field field = fieldDescriptor.getField();
		if(fieldDescriptor.isAccessByGet() || fieldDescriptor.isSearchFilterAccessByGet()) {
			String fieldName = field.getName();
			String methodName = "get" + Character.toUpperCase(fieldName.charAt(0));

			if(fieldName.length()>1) methodName+=fieldName.substring(1);
			try {
				Method m = entity.getClass().getDeclaredMethod(methodName, new Class<?>[]{});
				m.setAccessible(true);
				return m.invoke(entity, new Object[]{});
			} catch (SecurityException e) {
				throw new IndexObjectException("Could not access field by getter",e);
			} catch (NoSuchMethodException e) {
				throw new IndexObjectException("Could not access field by getter",e);
			} catch (IllegalArgumentException e) {
				throw new IndexObjectException("Could not access field by getter",e);
			} catch (IllegalAccessException e) {
				throw new IndexObjectException("Could not access field by getter",e);
			} catch (InvocationTargetException e) {
				throw new IndexObjectException("Could not access field by getter",e);
			}
		} else {
			try {
				field.setAccessible(true);
				return field.get(entity);
			} catch (IllegalArgumentException e) {
				throw new IndexObjectException("Could not access field",e);
			} catch (IllegalAccessException e) {
				throw new IndexObjectException("Could not access field",e);
			}			
		}
	}

	@SuppressWarnings("unchecked")
	protected Object applyTransform(MappedFieldDescriptor mappedFieldDescriptor, Object entityValue) {
		Transform<Object> transform = (Transform<Object>) mappedFieldDescriptor.getTransformation();

		if(transform!=null)	return transform.transform(entityValue);
		else return entityValue.toString();
	}

	protected String normalizeExpression(String text) {
		return TextLibrary.cleanSymbols(text).toUpperCase();
	}

	protected List<Term> tokenize(String text) {
		return TextLibrary.tokenize(text);
	}

	protected void deleteStopWords(List<Term> tokens, Language language) {
		TextLibrary.cleanStopWords(tokens, language.getIdentifier());
	}

	private void checkForConfigurationChange() throws IllegalStateException {
		if(search.isConfigured()==false)
			throw new IllegalStateException("Can't process objects when there's no active configuration");

		if(!search.getConfiguration().equals(configuration)) {
			configuration = search.getConfiguration();
		}
	}
}
