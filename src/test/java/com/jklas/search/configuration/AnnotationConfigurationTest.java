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
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.jklas.search.SearchEngine;
import com.jklas.search.annotations.IndexSelector;
import com.jklas.search.annotations.Indexable;
import com.jklas.search.annotations.LangId;
import com.jklas.search.annotations.LangSelector;
import com.jklas.search.annotations.SearchField;
import com.jklas.search.annotations.SearchFilter;
import com.jklas.search.annotations.SearchId;
import com.jklas.search.annotations.SearchSort;
import com.jklas.search.annotations.Stemming;
import com.jklas.search.annotations.TextProcessor;
import com.jklas.search.engine.Language;
import com.jklas.search.engine.processor.DefaultObjectTextProcessor;
import com.jklas.search.engine.processor.OneTermTextProcessor;
import com.jklas.search.engine.stemming.IdentityStemmerStrategy;
import com.jklas.search.engine.stemming.StemType;
import com.jklas.search.exception.SearchEngineException;
import com.jklas.search.exception.SearchEngineMappingException;
import com.jklas.search.indexer.IdentityTransform;

public class AnnotationConfigurationTest {

	private void applyMap(Object object, AnnotationConfigurationMapper mapper) {
		try {
			mapper.map(object);
		} catch (SearchEngineMappingException e) {
			Assert.fail("Search engine mapping error...");
			throw new RuntimeException(e);
		}
	}
	
	@Indexable
	private class DummyIndexable{}
		
	@Test
	public void testIndexableObjectIsIndexable() {
		DummyIndexable dummyIndexable = new DummyIndexable();
		
		AnnotationConfigurationMapper mapper = new AnnotationConfigurationMapper();
		
		Assert.assertTrue(mapper.isIndexable(dummyIndexable.getClass()));
	}
	
	@Test
	public void testIndexableObjectIsMapped() {		
		SearchEngine.getInstance().reset();
		
		DummyIndexable dummyIndexable = new DummyIndexable();
				
		SearchConfiguration currentConfiguration = SearchEngine.getInstance().newConfiguration();
		
		AnnotationConfigurationMapper mapper = new AnnotationConfigurationMapper();

		applyMap(dummyIndexable, mapper);
		
		Assert.assertTrue(currentConfiguration.isMapped(dummyIndexable.getClass()));
		
		Assert.assertTrue(currentConfiguration.getMapping(dummyIndexable.getClass()).isIndexable());
		
	}

	private class DummyNotIndexable {}
	
	@Test
	public void testNotIndexableObjectIsNotMapped() {		
		SearchEngine.getInstance().reset();
		
		DummyNotIndexable dummyNotIndexable = new DummyNotIndexable();
				
		SearchConfiguration currentConfiguration = SearchEngine.getInstance().newConfiguration();
		
		AnnotationConfigurationMapper mapper = new AnnotationConfigurationMapper();

		applyMap(dummyNotIndexable, mapper);
		
		Assert.assertFalse(currentConfiguration.isMapped(dummyNotIndexable));
		
	}

	@SuppressWarnings("unused")
	@Indexable
	private class DummyIndexableWithId {
		@SearchId
		private int id = 1;
		
		public int getId() {
			return id;
		}
	}

	@Test
	public void testSearchIdIsMapped() throws SecurityException, NoSuchFieldException {		
		SearchEngine.getInstance().reset();
		
		DummyIndexableWithId dummy = new DummyIndexableWithId();
				
		SearchConfiguration currentConfiguration = SearchEngine.getInstance().newConfiguration();
		
		AnnotationConfigurationMapper mapper = new AnnotationConfigurationMapper();

		applyMap(dummy, mapper);
		
		Field idField = DummyIndexableWithId.class.getDeclaredField("id");
		
		Assert.assertEquals(idField,currentConfiguration.getMapping(DummyIndexableWithId.class).getIdField());	
	}
	
	@SuppressWarnings("unused")
	@Indexable @LangId(value="spanish")  
	private class DummyIndexableWithSomeParameters {
		
		public static final String SEL_INDEX = "index";
		
		public static final String TRANSFORM_VALUE = "123465790";
		
		@SearchId
		private Integer id = 1;
		
		@IndexSelector
		private String indexSelector = SEL_INDEX;
			
		@SearchField
		@Stemming(strategy=IdentityStemmerStrategy.class, stemType=StemType.FULL_STEM)
		private String value = TRANSFORM_VALUE;
	}
	
	@Test
	public void testIndexSelectionAndLanguageIdIsMapped() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException, IllegalArgumentException, SearchEngineException {		
		SearchEngine.getInstance().reset();
		
		SearchConfiguration currentConfiguration = SearchEngine.getInstance().newConfiguration();

		AnnotationConfigurationMapper mapper = new AnnotationConfigurationMapper();

		DummyIndexableWithSomeParameters dummy = new DummyIndexableWithSomeParameters();
		
		applyMap(dummy, mapper);

		Assert.assertTrue(currentConfiguration.isMapped(dummy));
		
		Assert.assertEquals(new Language("SPANISH"),currentConfiguration.getMapping(dummy.getClass()).getLanguage());	
		
		Field idField = dummy.getClass().getDeclaredField("id");
		idField.setAccessible(true);
		
		Field mappedIdField = currentConfiguration.getMapping(dummy.getClass()).getIdField();
		mappedIdField.setAccessible(true);
		
		Assert.assertEquals(idField.get(dummy),mappedIdField.get(dummy));
		
		Field indexSelectorField = dummy.getClass().getDeclaredField("indexSelector");
		indexSelectorField .setAccessible(true);
		Assert.assertEquals(indexSelectorField.get(dummy),currentConfiguration.getMapping(dummy.getClass()).getIndexSelector().selectIndex(dummy).getIndexName());
		
		Field valueField = dummy.getClass().getDeclaredField("value");
		valueField.setAccessible(true);
		Assert.assertTrue(currentConfiguration.getMapping(dummy.getClass()).isMapped(valueField));
		
		Assert.assertEquals(IdentityTransform.getInstance(),currentConfiguration.getMapping(dummy.getClass()).getFieldDescriptor(valueField).getTransformation());
		
		Assert.assertEquals(IdentityStemmerStrategy.class,currentConfiguration.getMapping(dummy.getClass()).getFieldDescriptor(valueField).getStemmerStrategy().getClass());
	}
	
	@SuppressWarnings("unused")
	@Indexable @LangId(value="spanish")  
	private class DummyIndexableWithTwoLangs {
		
		@LangSelector
		private String lang = "english";
		
	}

	@Test
	public void testLangSelectorOverridesLangId() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException, IllegalArgumentException, SearchEngineException {		
		SearchEngine.getInstance().reset();
		
		SearchConfiguration currentConfiguration = SearchEngine.getInstance().newConfiguration();

		AnnotationConfigurationMapper mapper = new AnnotationConfigurationMapper();

		DummyIndexableWithTwoLangs dummy = new DummyIndexableWithTwoLangs();
		
		applyMap(dummy, mapper);

		Assert.assertTrue(currentConfiguration.getMapping(dummy).isLanguageSelectedByField());
	}
	
	@SuppressWarnings("unused")
	@Indexable
	private class DummyIndexableWithGetAccess {		
		@SearchField(accessByGet=true)
		private String value = "something";		
		public String getLang() {
			return value;
		}
	}
	
	@SuppressWarnings("unused")
	@Indexable
	private class DummyIndexableWithoutGetAccess {
		@SearchField(accessByGet=false)
		private String value = "something";
		public String getLang() {
			return value;
		}		
	}
	
	@Test
	public void testAccessByGet() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException, IllegalArgumentException, SearchEngineException {
		SearchEngine.getInstance().reset();
		SearchConfiguration currentConfiguration = SearchEngine.getInstance().newConfiguration();
		
		AnnotationConfigurationMapper mapper = new AnnotationConfigurationMapper();

		DummyIndexableWithGetAccess dummyWithGet = new DummyIndexableWithGetAccess();
		DummyIndexableWithoutGetAccess dummyWithoutGet = new DummyIndexableWithoutGetAccess();
		
		applyMap(dummyWithGet, mapper);
		
		applyMap(dummyWithoutGet, mapper);

		Field valueField = dummyWithGet.getClass().getDeclaredField("value");
		
		Assert.assertTrue(currentConfiguration.getMapping(dummyWithGet).getFieldDescriptor(valueField).isAccessByGet());
		
		valueField = dummyWithoutGet.getClass().getDeclaredField("value");
		
		Assert.assertFalse(currentConfiguration.getMapping(dummyWithoutGet).getFieldDescriptor(valueField).isAccessByGet());
	}
	
	@SuppressWarnings("unused")
	@Indexable
	private class DummyIndexableWith2IndexFields {
		@SearchField
		private String value0= "something0";
		@SearchField
		private String value1= "something1";
	
		private String value2= "something2";
	}
	
	@Test
	public void testOnlyTwoFieldsMustBeMapped() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException, IllegalArgumentException, SearchEngineException {
		SearchEngine.getInstance().reset();
		SearchConfiguration currentConfiguration = SearchEngine.getInstance().newConfiguration();
		
		AnnotationConfigurationMapper mapper = new AnnotationConfigurationMapper();

		DummyIndexableWith2IndexFields dummy = new DummyIndexableWith2IndexFields();
		
		applyMap(dummy, mapper);
		
		Assert.assertTrue(currentConfiguration.getMapping(dummy).getMappedFields().entrySet().size() == 2);
		
	}
	
	@SuppressWarnings("unused")
	@Indexable
	private class DummyWithFilterFieldFields {
		@SearchField @SearchFilter		
		private String value0= "something0";
		
		@SearchField  @SearchFilter(accessByGet=true)
		private String value1= "something1";
		
		@SearchFilter
		private String value2= "something2";
	}
	
	@Test
	public void testSearchFilterIsMapped() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException, IllegalArgumentException, SearchEngineException {
		SearchEngine.getInstance().reset();
		
		SearchConfiguration currentConfiguration = SearchEngine.getInstance().newConfiguration();
		
		AnnotationConfigurationMapper mapper = new AnnotationConfigurationMapper();

		DummyWithFilterFieldFields dummy = new DummyWithFilterFieldFields();
		
		applyMap(dummy, mapper);
		
		Field value0 = DummyWithFilterFieldFields.class.getDeclaredField("value0");
		Field value1 = DummyWithFilterFieldFields.class.getDeclaredField("value1");
		Field value2 = DummyWithFilterFieldFields.class.getDeclaredField("value2");
		
		Assert.assertEquals(3, currentConfiguration.getMapping(dummy).getMappedFields().size());
		
		Assert.assertTrue(currentConfiguration.getMapping(dummy).getFieldDescriptor(value0).isFilterField());
		Assert.assertFalse(currentConfiguration.getMapping(dummy).getFieldDescriptor(value0).isSearchFilterAccessByGet());
		
		Assert.assertTrue(currentConfiguration.getMapping(dummy).getFieldDescriptor(value1).isFilterField());
		Assert.assertTrue(currentConfiguration.getMapping(dummy).getFieldDescriptor(value1).isSearchFilterAccessByGet());
		
		Assert.assertTrue(currentConfiguration.getMapping(dummy).getFieldDescriptor(value2).isFilterField());
	}
	
	@Test
	public void testSearchFilterIsNotTrueByDefault() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException, IllegalArgumentException, SearchEngineException {
		SearchEngine.getInstance().reset();
		
		SearchConfiguration currentConfiguration = SearchEngine.getInstance().newConfiguration();
		
		AnnotationConfigurationMapper mapper = new AnnotationConfigurationMapper();

		DummyIndexableWithGetAccess dummy = new DummyIndexableWithGetAccess();
		
		applyMap(dummy, mapper);
		
		Field value = DummyIndexableWithGetAccess.class.getDeclaredField("value");
		
		Assert.assertFalse(currentConfiguration.getMapping(dummy).getFieldDescriptor(value).isFilterField());
		
	}

	@SuppressWarnings("unused")
	@Indexable
	private class DummyWithSortFields {
		@SearchField @SearchSort
		private String value0= "something0";
		
		@SearchField  @SearchSort(accessByGet=true)
		private String value1= "something1";
		
		@SearchSort
		private String value2= "something2";
		
		@SearchField
		private String value3= "something3";
	}
	
	@Test
	public void testSearchSortIsNotTrueByDefault() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException, IllegalArgumentException, SearchEngineException {
		SearchEngine.getInstance().reset();
		
		SearchConfiguration currentConfiguration = SearchEngine.getInstance().newConfiguration();
		
		AnnotationConfigurationMapper mapper = new AnnotationConfigurationMapper();

		DummyWithSortFields dummy = new DummyWithSortFields();
		
		applyMap(dummy, mapper);
		
		Field value = DummyWithSortFields.class.getDeclaredField("value3");
		
		Assert.assertFalse(currentConfiguration.getMapping(dummy).getFieldDescriptor(value).isSortField());
		
	}

	@Test
	public void testSearchSortIsMapped() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException, IllegalArgumentException, SearchEngineException {
		SearchEngine.getInstance().reset();
		
		SearchConfiguration currentConfiguration = SearchEngine.getInstance().newConfiguration();
		
		AnnotationConfigurationMapper mapper = new AnnotationConfigurationMapper();

		DummyWithSortFields dummy = new DummyWithSortFields();
		
		applyMap(dummy, mapper);
		
		Field value0 = DummyWithSortFields.class.getDeclaredField("value0");
		Field value1 = DummyWithSortFields.class.getDeclaredField("value1");
		Field value2 = DummyWithSortFields.class.getDeclaredField("value2");
		Field value3 = DummyWithSortFields.class.getDeclaredField("value3");
		
		Assert.assertEquals(4, currentConfiguration.getMapping(dummy).getMappedFields().size());
		
		Assert.assertTrue(currentConfiguration.getMapping(dummy).getFieldDescriptor(value0).isSortField());
		Assert.assertFalse(currentConfiguration.getMapping(dummy).getFieldDescriptor(value0).isSortFilterAccessByGet());
		
		Assert.assertTrue(currentConfiguration.getMapping(dummy).getFieldDescriptor(value1).isSortField());
		Assert.assertTrue(currentConfiguration.getMapping(dummy).getFieldDescriptor(value1).isSortFilterAccessByGet());
		
		Assert.assertTrue(currentConfiguration.getMapping(dummy).getFieldDescriptor(value2).isSortField());
		
		Assert.assertFalse(currentConfiguration.getMapping(dummy).getFieldDescriptor(value3).isSortField());
	}
	
	@SuppressWarnings("unused")
	@Indexable @TextProcessor(OneTermTextProcessor.class)
	private class TextProcessorDummy {
		@SearchId
		private int id = 1;
		
		@SearchField
		private String attribute1;
		
		@SearchField
		@TextProcessor(DefaultObjectTextProcessor.class)
		private String attribute2;
		
		@SearchFilter
		private Date filterValue = new Date(0);
		
		@SearchSort
		private double sortValue = 1.2d;		
	}
	
	@Test
	public void testTextProcessorIsMapped() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException, IllegalArgumentException, SearchEngineException {
		SearchEngine.getInstance().reset();
		
		SearchConfiguration currentConfiguration = SearchEngine.getInstance().newConfiguration();
		
		AnnotationConfigurationMapper mapper = new AnnotationConfigurationMapper();

		TextProcessorDummy dummy = new TextProcessorDummy();
		
		applyMap(dummy, mapper);
		
		Assert.assertEquals(OneTermTextProcessor.class, currentConfiguration.getMapping(dummy).getTextProcessor().getClass());
		
		Field attribute1 = TextProcessorDummy.class.getDeclaredField("attribute1");
		
		Field attribute2 = TextProcessorDummy.class.getDeclaredField("attribute2");
		
		Assert.assertEquals(OneTermTextProcessor.class, currentConfiguration.getMapping(dummy).getFieldDescriptor(attribute1).getTextProcessor().getClass());
		
		Assert.assertEquals(DefaultObjectTextProcessor.class, currentConfiguration.getMapping(dummy).getFieldDescriptor(attribute2).getTextProcessor().getClass());
	}
	
}
