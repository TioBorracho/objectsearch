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
package com.jklas.search.indexer;

import java.lang.reflect.Field;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jklas.search.SearchEngine;
import com.jklas.search.annotations.IndexSelector;
import com.jklas.search.annotations.Indexable;
import com.jklas.search.annotations.SearchField;
import com.jklas.search.annotations.SearchFilter;
import com.jklas.search.annotations.SearchId;
import com.jklas.search.configuration.AnnotationConfigurationMapper;
import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.exception.SearchEngineException;
import com.jklas.search.exception.SearchEngineMappingException;
import com.jklas.search.index.IndexId;
import com.jklas.search.index.MasterAndInvertedIndex;
import com.jklas.search.index.ObjectKey;
import com.jklas.search.index.PostingList;
import com.jklas.search.index.PostingMetadata;
import com.jklas.search.index.Term;
import com.jklas.search.index.memory.MemoryIndex;
import com.jklas.search.index.memory.MemoryIndexWriterFactory;
import com.jklas.search.indexer.pipeline.DefaultIndexingPipeline;

public class DefaultIndexerServiceTest {

	
	@Indexable
	private class DummyWithIdAndAttributes {
		@SuppressWarnings("unused")
		@SearchId
		private int id = 1;
		@SuppressWarnings("unused")
		@SearchField
		private String value = "JULIAN";
		
		public void setValue(String value) {
			this.value = value;
		}
	}
	
	@Before
	public void renewDefaultIndex() {
		SearchEngine.getInstance().reset();
		MemoryIndex.newDefaultIndex();
	}
	
	// -- CREATE
	
	@Test
	public void testCreateNullResultsInException() throws IndexObjectException, SearchEngineMappingException {
		
		SearchEngine.getInstance().newConfiguration();
		
		DefaultIndexingPipeline indexingPipeline = new DefaultIndexingPipeline();
		
		DefaultIndexerService idxService = new DefaultIndexerService(indexingPipeline, MemoryIndexWriterFactory.getInstance());
		
		try {
			idxService.create(null);			
		}catch(IndexObjectException ioe){
			Assert.assertTrue(true);
			return;
		}catch(Exception e){
			Assert.fail();	
			return;
		}
		
		Assert.fail();		
	}
	
	@Test
	public void testCreatedObjectIsWrittenOnIndex() throws IndexObjectException, SearchEngineMappingException {
		
		DummyWithIdAndAttributes dummy = new DummyWithIdAndAttributes();
		
		AnnotationConfigurationMapper.configureAndMap(dummy);
		
		DefaultIndexingPipeline indexingPipeline = new DefaultIndexingPipeline();
		
		DefaultIndexerService idxService = new DefaultIndexerService(indexingPipeline, MemoryIndexWriterFactory.getInstance());
		
		idxService.create(dummy);
		
		PostingList julianList = MemoryIndex.getDefaultIndex().getPostingList(new Term("JULIAN"));
		
		Assert.assertTrue( julianList.size() == 1 );		
	}
	
	@Test
	public void testCreateSameObjectTwiceResultsInOnlyOneObjectAdded() throws IndexObjectException, SearchEngineMappingException, SearchEngineException {
		
		DummyWithIdAndAttributes dummy = new DummyWithIdAndAttributes();
		
		AnnotationConfigurationMapper.configureAndMap(dummy);
		
		MemoryIndex memoryIndex = MemoryIndex.getDefaultIndex();
		DefaultIndexingPipeline indexingPipeline = new DefaultIndexingPipeline();
		
		DefaultIndexerService idxService = new DefaultIndexerService(indexingPipeline, MemoryIndexWriterFactory.getInstance());
		
		idxService.create(dummy);
		idxService.create(dummy);
		
		PostingList julianList = memoryIndex.getPostingList(new Term("JULIAN"));
		
		Assert.assertTrue( julianList.size() == 1 );		
	}

	@Test
	public void testSameClassObjectsWithDifferentValuesProducesDifferentPostings() throws IndexObjectException, SearchEngineMappingException {
		
		DummyWithIdAndAttributes dummy = new DummyWithIdAndAttributes();
		
		AnnotationConfigurationMapper.configureAndMap(dummy);
		
		MasterAndInvertedIndex memoryIndex = MemoryIndex.getDefaultIndex();
		DefaultIndexerService idxService = getDefaultIndexServiceForMemoryIndex(memoryIndex);
				
		for (int i = 0; i < 100; i++) {
			dummy.setValue(String.valueOf(i));
			idxService.create(dummy);			
		}
		
		Assert.assertTrue( memoryIndex.getTermDictionarySize() == 100);
		
		for (int i = 0; i < 100; i++) {
			Assert.assertTrue( memoryIndex.getPostingList(new Term(String.valueOf(i))).size()==1);
		}
		
	}
	
	@SuppressWarnings("unused")
	
	@Indexable
	private class Class1 { @SearchId public int id = 1; @SearchField public String value = "V"; }
	
	@SuppressWarnings("unused")
	
	@Indexable
	private class Class2 { @SearchId public int id = 1; @SearchField public String value = "V"; }
	
	@Test
	public void testObjectsFromDifferentClassesProducesDifferentsPostings() throws SearchEngineMappingException, IndexObjectException {
		Class1 class1 = new Class1();
		Class2 class2 = new Class2();
		AnnotationConfigurationMapper.configureAndMap(class1);
		AnnotationConfigurationMapper.configureAndMap(class2);
		
		MasterAndInvertedIndex memoryIndex = MemoryIndex.getDefaultIndex();
		DefaultIndexerService idxService = getDefaultIndexServiceForMemoryIndex(memoryIndex);
				
		idxService.create(class1);
		idxService.create(class2);
		
		Assert.assertTrue(2 == memoryIndex.getPostingList(new Term("V")).size());
	}

	
	// -- CREATE + UPDATE
	@Test
	public void testCreateAndTrivialUpdateDoesntModifiesIndex() throws IndexObjectException, SearchEngineMappingException {
		
		DummyWithIdAndAttributes dummy = new DummyWithIdAndAttributes();
		
		AnnotationConfigurationMapper.configureAndMap(dummy);
		
		MemoryIndex memoryIndex = MemoryIndex.getDefaultIndex();
		DefaultIndexingPipeline indexingPipeline = new DefaultIndexingPipeline();
		
		DefaultIndexerService idxService = new DefaultIndexerService(indexingPipeline, MemoryIndexWriterFactory.getInstance());
		
		// created 
		idxService.create(dummy);
		Assert.assertTrue( memoryIndex.getTermDictionarySize() == 1);		
		PostingList julianList = memoryIndex.getPostingList(new Term("JULIAN"));
		Assert.assertTrue( julianList.size() == 1 );
		

		// updated
		idxService.update(dummy);
		Assert.assertTrue( memoryIndex.getTermDictionarySize() == 1);
		julianList = memoryIndex.getPostingList(new Term("JULIAN"));
		Assert.assertTrue( julianList.size() == 1 );		
	}

	@Test
	public void testCreateAndUpdateLeavesNoTracesOfOldTerms() throws IndexObjectException, SearchEngineMappingException {
		
		DummyWithIdAndAttributes dummy = new DummyWithIdAndAttributes();
		
		AnnotationConfigurationMapper.configureAndMap(dummy);
		
		MemoryIndex memoryIndex = MemoryIndex.getDefaultIndex();	
		DefaultIndexerService idxService = getDefaultIndexServiceForMemoryIndex(memoryIndex);
		
		// created 
		dummy.setValue("JULIAN");
		idxService.create(dummy);
		
		Assert.assertTrue( memoryIndex.getTermDictionarySize() == 1);
		Assert.assertNotNull(memoryIndex.getPostingList(new Term("JULIAN")));
		Assert.assertTrue( memoryIndex.getPostingList(new Term("JULIAN")).size() == 1 );		
		Assert.assertNull( memoryIndex.getPostingList(new Term("KLAS")));
		
		// updated
		dummy.setValue("KLAS");
		idxService.update(dummy);
		Assert.assertTrue( memoryIndex.getTermDictionarySize() == 1);
		Assert.assertNotNull( memoryIndex.getPostingList(new Term("KLAS")) );
		Assert.assertTrue( memoryIndex.getPostingList(new Term("KLAS")).size() == 1 );
		Assert.assertNull( memoryIndex.getPostingList(new Term("JULIAN")));		
	}
	
	@SuppressWarnings("unused")
	// -- CREATE + DELETE

	// Other high level tests
	@Indexable
	private class DummyWithSameValueFields {
		@SearchId public int id =1;
		@SearchField public String name="MARTIN";
		@SearchField public String lastname="MARTIN";
	}
	
	@Test
	public void testTwoFieldsWithSameValueProduceOnePostingWithTwoFieldsInMetadata() throws IndexObjectException, SearchEngineMappingException, SecurityException, NoSuchFieldException {
		MemoryIndex memoryIndex = MemoryIndex.getDefaultIndex();
		DummyWithSameValueFields entity = new DummyWithSameValueFields();
		
		AnnotationConfigurationMapper.configureAndMap(entity);
		
		IndexerService idxService = getDefaultIndexServiceForMemoryIndex(memoryIndex);
		
		idxService.create(entity);
		
		PostingList postingList = memoryIndex.getPostingList(new Term("MARTIN"));
		
		for (Entry<ObjectKey, PostingMetadata> entry : postingList) {
			Assert.assertEquals(2,entry.getValue().getSourceFields().size());
			Assert.assertTrue(entry.getValue().getSourceFields().contains(entity.getClass().getDeclaredField("name")));
			Assert.assertTrue(entry.getValue().getSourceFields().contains(entity.getClass().getDeclaredField("lastname")));
		}
	}
	
	@SuppressWarnings("unused")
	
	@Indexable
	private class DummyForIndexA {
		@SearchId public int id =1;
		
		@IndexSelector public String indexToSelect = "A";
		
		@SearchField public String name="MARTIN";
		
	}
	
	@SuppressWarnings("unused")
	
	@Indexable
	private class DummyForIndexB {
		@SearchId public int id =1;
		
		@IndexSelector public String indexToSelect = "B";
		
		@SearchField public String name="MARTIN";
		
	}
	
	@Test
	public void testObjectsGoToSelectedIndex() throws IndexObjectException, SearchEngineMappingException {
		
		MemoryIndex.renewAllIndexes();		
		
		DummyForIndexA entityA = new DummyForIndexA();
		DummyForIndexB entityB = new DummyForIndexB();
		
		AnnotationConfigurationMapper.configureAndMap(entityA);
		AnnotationConfigurationMapper.configureAndMap(entityB);
		
		DefaultIndexingPipeline indexingPipeline = new DefaultIndexingPipeline();		
		DefaultIndexerService idxService = new DefaultIndexerService(indexingPipeline, MemoryIndexWriterFactory.getInstance());
				
		idxService.create(entityA);
		idxService.create(entityB);
		
		Assert.assertNotNull(MemoryIndex.getIndex(new IndexId("A")));
		Assert.assertNotNull(MemoryIndex.getIndex(new IndexId("B")));
		
		Assert.assertNotNull(MemoryIndex.getIndex(new IndexId("A")).getPostingList(new Term("MARTIN")));
		Assert.assertEquals(MemoryIndex.getIndex(new IndexId("A")).getObjectCount(),1);
		
		Assert.assertNotNull(MemoryIndex.getIndex(new IndexId("B")).getPostingList(new Term("MARTIN")));
		Assert.assertEquals(MemoryIndex.getIndex(new IndexId("B")).getObjectCount(),1);
				
	}
	
	@SuppressWarnings("unused")
	
	@Indexable
	private class DummyIndexSelector {
		@SearchId public int id =1;
		
		@IndexSelector public String indexToSelect;
		
		@SearchField public String name="MARTIN";
		
	}
	
	@Test
	public void testSameObjectCanGoToDifferentSelectedIndexes() throws IndexObjectException, SearchEngineMappingException {
		
		MemoryIndex.newIndex(new IndexId("A"));
		MemoryIndex.newIndex(new IndexId("B"));
		
		DummyIndexSelector entity = new DummyIndexSelector();
		
		AnnotationConfigurationMapper.configureAndMap(entity);
		
		DefaultIndexingPipeline indexingPipeline = new DefaultIndexingPipeline();		
		DefaultIndexerService idxService = new DefaultIndexerService(indexingPipeline, MemoryIndexWriterFactory.getInstance());
		
		entity.indexToSelect = "A";
		idxService.create(entity);
		
		entity.indexToSelect = "B";
		idxService.create(entity);
		
		Assert.assertNotNull(MemoryIndex.getIndex(new IndexId("A")));
		Assert.assertNotNull(MemoryIndex.getIndex(new IndexId("B")));
		
		Assert.assertNotNull(MemoryIndex.getIndex(new IndexId("A")).getPostingList(new Term("MARTIN")));
		Assert.assertEquals(MemoryIndex.getIndex(new IndexId("A")).getObjectCount(),1);
		
		Assert.assertNotNull(MemoryIndex.getIndex(new IndexId("B")).getPostingList(new Term("MARTIN")));
		Assert.assertEquals(MemoryIndex.getIndex(new IndexId("B")).getObjectCount(),1);
				
	}
	
	@Test
	public void testObjectAddedToIndexIsDeletedFromSameIndex() throws IndexObjectException, SearchEngineMappingException {
		
		MemoryIndex.newIndex(new IndexId("A"));
		
		DummyIndexSelector entity = new DummyIndexSelector();
		
		AnnotationConfigurationMapper.configureAndMap(entity);
		
		DefaultIndexingPipeline indexingPipeline = new DefaultIndexingPipeline();		
		DefaultIndexerService idxService = new DefaultIndexerService(indexingPipeline, MemoryIndexWriterFactory.getInstance());
		
		entity.indexToSelect = "A";
		idxService.create(entity);		
		
		Assert.assertNotNull(MemoryIndex.getIndex(new IndexId("A")));
		
		Assert.assertNotNull(MemoryIndex.getIndex(new IndexId("A")).getPostingList(new Term("MARTIN")));
		Assert.assertEquals(MemoryIndex.getIndex(new IndexId("A")).getObjectCount(),1);
		
		idxService.delete(entity);
		
		Assert.assertNull(MemoryIndex.getIndex(new IndexId("A")).getPostingList(new Term("MARTIN")));
		Assert.assertEquals(MemoryIndex.getIndex(new IndexId("A")).getObjectCount(),0);
		
	}

	@SuppressWarnings("unused")
	@Indexable
	private class DummyWithFilterFieldFields {
		
		public DummyWithFilterFieldFields(int id) {
			this.id = id;
		}
		
		@SearchId
		private int id;
		
		@SearchField @SearchFilter		
		private String value0= "something0";
		
		@SearchField  @SearchFilter(accessByGet=true)
		private String value1= "something1";
		
		@SearchFilter
		private String value2= "something2";
		
		public String getValue1() {
			return value1;
		}
	}	
	
	@Test
	public void testIndexFiltrableFieldValueIsStored() throws IndexObjectException, SearchEngineMappingException, SecurityException, NoSuchFieldException {
		
		DummyWithFilterFieldFields entity = new DummyWithFilterFieldFields(0);
		
		AnnotationConfigurationMapper.configureAndMap(entity);
				
		Field value0 = DummyWithFilterFieldFields.class.getDeclaredField("value0");
		Field value1 = DummyWithFilterFieldFields.class.getDeclaredField("value1");
		Field value2 = DummyWithFilterFieldFields.class.getDeclaredField("value2");
		
		DefaultIndexingPipeline indexingPipeline = new DefaultIndexingPipeline();		
		DefaultIndexerService idxService = new DefaultIndexerService(indexingPipeline, MemoryIndexWriterFactory.getInstance());
		
		idxService.create(entity);
		
		PostingList something0PostingList = MemoryIndex.getDefaultIndex().getPostingList(new Term("SOMETHING0"));
		
		for (Entry<ObjectKey, PostingMetadata> entry : something0PostingList) {
			PostingMetadata metadata = entry.getValue();
			Object something0 = metadata.getStoredFieldValue(value0);
			Assert.assertEquals("something0", something0);
			
			Object something1 = metadata.getStoredFieldValue(value1);
			Assert.assertEquals("something1", something1);
			
			Object something2 = metadata.getStoredFieldValue(value2);
			Assert.assertEquals("something2", something2);
		}
		
		
	}
	
	
	private DefaultIndexerService getDefaultIndexServiceForMemoryIndex(MasterAndInvertedIndex memoryIndex) {
		DefaultIndexingPipeline indexingPipeline = new DefaultIndexingPipeline();		
		DefaultIndexerService idxService = new DefaultIndexerService(indexingPipeline, MemoryIndexWriterFactory.getInstance());
		return idxService;
	}
	

}
