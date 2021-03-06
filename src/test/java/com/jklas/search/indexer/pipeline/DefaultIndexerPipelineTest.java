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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.jklas.search.annotations.IndexReference;
import com.jklas.search.annotations.Indexable;
import com.jklas.search.annotations.IndexableContainer;
import com.jklas.search.annotations.LangId;
import com.jklas.search.annotations.SearchCollection;
import com.jklas.search.annotations.SearchContained;
import com.jklas.search.annotations.SearchField;
import com.jklas.search.annotations.SearchFilter;
import com.jklas.search.annotations.SearchId;
import com.jklas.search.annotations.SearchSort;
import com.jklas.search.annotations.Stemming;
import com.jklas.search.annotations.TextProcessor;
import com.jklas.search.configuration.AnnotationConfigurationMapper;
import com.jklas.search.engine.Language;
import com.jklas.search.engine.operations.StopWordCleaner;
import com.jklas.search.engine.operations.StopWordProvider;
import com.jklas.search.engine.processor.DefaultObjectTextProcessor;
import com.jklas.search.engine.processor.NullProcessor;
import com.jklas.search.engine.processor.OneTermTextProcessor;
import com.jklas.search.engine.stemming.EnglishSnowballStemmingStrategy;
import com.jklas.search.engine.stemming.SpanishSnowballStemmingStrategy;
import com.jklas.search.engine.stemming.StemType;
import com.jklas.search.engine.stemming.snowball.SpanishLightStemmer;
import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.exception.SearchEngineMappingException;
import com.jklas.search.index.PostingMetadata;
import com.jklas.search.index.Term;
import com.jklas.search.index.dto.IndexObject;
import com.jklas.search.util.Utils;
import com.jklas.search.util.Utils.SingleAttributeEntity;

public class DefaultIndexerPipelineTest {

	@SuppressWarnings("unused")
	
	@Indexable
	private class Dummy {@SearchId public Serializable id = IndexObject.NO_ID;}
	
	@Test
	public void testEmptyObjectProducesZeroObjectEntries() throws IndexObjectException, SearchEngineMappingException {
		
		Dummy dummy = new Dummy();
		
		Utils.configureAndMap(dummy);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		IndexObject dto =  new IndexObject(dummy, IndexObject.NO_ID);
		
		SemiIndex semiIndex = pipeline.processObject(dummy);
		
		Map<Term, PostingMetadata> objectEntries = semiIndex.getSemiIndexMap().get(dto);
		
		Assert.assertTrue( objectEntries.size() == 0 );
		
	}

	@Test
	public void testNotIndexableObjectProducesZeroObjectEntries() throws IndexObjectException, SearchEngineMappingException {
		
		Object notIndexableEntity = new Object();
		
		Utils.configureAndMap(notIndexableEntity);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		SemiIndex semiIndex = pipeline.processObject(notIndexableEntity);
		
		IndexObject dto =  new IndexObject(notIndexableEntity, IndexObject.NO_ID);
		
		Map<Term, PostingMetadata> objectEntries = semiIndex.getSemiIndexMap().get(dto);

		Assert.assertEquals(0, objectEntries.size());
		
	}

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
	
	@Test
	public void testSingleAttributeAndWordProducesSingleEntry() throws IndexObjectException, SearchEngineMappingException {
		
		DummyWithIdAndAttributes dummy = new DummyWithIdAndAttributes();
		
		Utils.configureAndMap(dummy);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		SemiIndex semiIndex = pipeline.processObject(dummy);
		
		IndexObject dto =  new IndexObject(dummy);
		
		Map<Term, PostingMetadata> objectEntries = semiIndex.getSemiIndexMap().get(dto);
		
		Assert.assertEquals( 1, objectEntries.size() );
		
		for ( Map.Entry<Term, PostingMetadata> postings	: objectEntries.entrySet() ) {
			Assert.assertNotNull( postings.getValue() );
		}
		
	}
	
	@Test
	public void testTwoWordsProducesTwoTerms() throws IndexObjectException, SearchEngineMappingException {
		
		DummyWithIdAndAttributes dummy = new DummyWithIdAndAttributes();
		
		Utils.configureAndMap(dummy);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		dummy.setValue("TWO WORDS");
		
		SemiIndex semiIndex = pipeline.processObject(dummy);
		
		IndexObject dto =  new IndexObject(dummy);
		
		Map<Term, PostingMetadata> objectEntries = semiIndex.getSemiIndexMap().get(dto);
		
		Assert.assertEquals( 2, objectEntries.size() );
		
		for ( Map.Entry<Term, PostingMetadata> postings	: objectEntries.entrySet() ) {
			Assert.assertNotNull( postings.getValue() );
		}
		
	}
	
	@Test
	public void testSameWordTwiceProducesOnlyOneTerm() throws IndexObjectException, SearchEngineMappingException {
		
		DummyWithIdAndAttributes dummy = new DummyWithIdAndAttributes();
		
		Utils.configureAndMap(dummy);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		dummy.setValue("SAME SAME");
		
		SemiIndex semiIndex = pipeline.processObject(dummy);
		
		IndexObject dto =  new IndexObject(dummy);
		
		Map<Term, PostingMetadata> termPostingMap = semiIndex.getSemiIndexMap().get(dto);
		
		Assert.assertEquals( 1, termPostingMap.size() );
		
		for ( Map.Entry<Term, PostingMetadata> postings	: termPostingMap.entrySet() ) {
			Assert.assertNotNull( postings.getValue() );
		}
		
	}
	
	@Test
	public void testNullValueProducesZeroTerms() throws IndexObjectException, SearchEngineMappingException {
		
		DummyWithIdAndAttributes dummy = new DummyWithIdAndAttributes();
		
		Utils.configureAndMap(dummy);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		dummy.setValue(null);
		
		SemiIndex semiIndex = pipeline.processObject(dummy);
		
		IndexObject dto =  new IndexObject(dummy);
		
		Map<Term, PostingMetadata> objectEntries = semiIndex.getSemiIndexMap().get(dto);

		Assert.assertEquals( 0, objectEntries.size() );
	}
	
	@Test
	public void testZeroSizeValueProducesZeroTerms() throws IndexObjectException, SearchEngineMappingException {
		
		DummyWithIdAndAttributes dummy = new DummyWithIdAndAttributes();
		
		Utils.configureAndMap(dummy);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		dummy.setValue("");
		
		SemiIndex semiIndex = pipeline.processObject(dummy);
		
		IndexObject dto =  new IndexObject(dummy);
		
		Map<Term, PostingMetadata> objectEntries = semiIndex.getSemiIndexMap().get(dto);
		
		Assert.assertEquals( 0, objectEntries.size() );
	}	
	
	@Test
	public void testTwoPairsOfWordsProducesOnlyTwoTerms() throws IndexObjectException, SearchEngineMappingException {
		
		DummyWithIdAndAttributes dummy = new DummyWithIdAndAttributes();
		
		Utils.configureAndMap(dummy);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		dummy.setValue("SAME WORD WORD SAME");
		
		SemiIndex semiIndex = pipeline.processObject(dummy);
		
		IndexObject dto =  new IndexObject(dummy);
		
		Map<Term, PostingMetadata> termPostingMap = semiIndex.getSemiIndexMap().get(dto);
		
		Assert.assertEquals( 2, termPostingMap.size() );
		
		for ( Map.Entry<Term, PostingMetadata> postings	: termPostingMap.entrySet() ) {
			Assert.assertNotNull( postings.getValue() );
		}
		
	}
	
	@Test
	public void testUppercaseAndLowercaseAreTheSame() throws IndexObjectException, SearchEngineMappingException {
		
		DummyWithIdAndAttributes dummy = new DummyWithIdAndAttributes();
		
		Utils.configureAndMap(dummy);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		dummy.setValue("SAME same");
		
		SemiIndex semiIndex = pipeline.processObject(dummy);
		
		IndexObject dto =  new IndexObject(dummy);
		
		Map<Term, PostingMetadata> termPostingMap = semiIndex.getSemiIndexMap().get(dto);
		
		Assert.assertEquals( 1, termPostingMap.size() );
		
		for ( Map.Entry<Term, PostingMetadata> postings	: termPostingMap.entrySet() ) {
			Assert.assertNotNull( postings.getValue() );
		}
		
	}
	
	@Indexable
	private class EnglishSnowballDummy {
		@SuppressWarnings("unused")
		@SearchId
		private int id = 1;
		@SuppressWarnings("unused")
		@SearchField @Stemming(stemType=StemType.FULL_STEM,strategy=EnglishSnowballStemmingStrategy.class)
		private String value = "JULIAN";
		
		public void setValue(String value) {
			this.value = value;
		}
	}
		
	@Test
	public void testSnowballStemmerForEnglishWord() throws IndexObjectException, SearchEngineMappingException {
		
		EnglishSnowballDummy dummy = new EnglishSnowballDummy();
		
		Utils.configureAndMap(dummy);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		dummy.setValue("Works");
				
		SemiIndex semiIndex = pipeline.processObject(dummy);
		
		IndexObject dto =  new IndexObject(dummy);
		
		Map<Term, PostingMetadata> termPostingMap = semiIndex.getSemiIndexMap().get(dto);
		
		Assert.assertEquals( 1, termPostingMap.size() );
		
		for ( Map.Entry<Term, PostingMetadata> postings	: termPostingMap.entrySet() ) {
			Assert.assertEquals( postings.getKey().getValue(), "Work".toUpperCase() );
		}
		
	}
	
	@Indexable
	private class SpanishSnowballDummy {
		@SuppressWarnings("unused")
		@SearchId
		private int id = 1;
		
		@SuppressWarnings("unused")
		@SearchField @Stemming(stemType=StemType.FULL_STEM,strategy=SpanishSnowballStemmingStrategy.class)
		private String value = "";
		
		public void setValue(String value) {
			this.value = value;
		}
	}
		
	@Test
	public void testSnowballFullStemmerForSpanishWord() throws IndexObjectException, SearchEngineMappingException {
		
		SpanishSnowballDummy dummy = new SpanishSnowballDummy();
		
		Utils.configureAndMap(dummy);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		dummy.setValue("TRABAJOS");
				
		SemiIndex semiIndex = pipeline.processObject(dummy);
		
		IndexObject dto =  new IndexObject(dummy);
		
		Map<Term, PostingMetadata> termPostingMap = semiIndex.getSemiIndexMap().get(dto);
		
		Assert.assertEquals( 1, termPostingMap.size() );
		
		for ( Map.Entry<Term, PostingMetadata> postings	: termPostingMap.entrySet() ) {
			Assert.assertEquals( postings.getKey().getValue(), "TRABAJ".toUpperCase() );
		}
		
	}
	
	@Indexable
	private class SpanishNumberSnowballStemDummy {
		@SuppressWarnings("unused")
		@SearchId
		private int id = 1;
		
		@SuppressWarnings("unused")
		@SearchField @Stemming(stemType=StemType.LIGHT_STEM,strategy=SpanishSnowballStemmingStrategy.class)
		private String value = "";
		
		public void setValue(String value) {
			this.value = value;
		}
	}
		
	@Test
	public void testSnowballNumberStemmerForSpanishWord() throws IndexObjectException, SearchEngineMappingException {
		
		SpanishNumberSnowballStemDummy dummy = new SpanishNumberSnowballStemDummy();
		
		Utils.configureAndMap(dummy);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		dummy.setValue("trabajos");
				
		SemiIndex semiIndex = pipeline.processObject(dummy);
		
		IndexObject dto =  new IndexObject(dummy);
		
		Map<Term, PostingMetadata> termPostingMap = semiIndex.getSemiIndexMap().get(dto);
		
		Assert.assertEquals( 1, termPostingMap.size() );
		
		SpanishLightStemmer spanishNumberStemmer = new SpanishLightStemmer();
		Term directStem = spanishNumberStemmer.stem(new Term("trabajos"));
		
		for ( Map.Entry<Term, PostingMetadata> postings	: termPostingMap.entrySet() ) {
			Assert.assertEquals( postings.getKey().getValue(), directStem.getValue().toUpperCase() );
		}
		
	}
	
	@SuppressWarnings("unused")
	@Indexable
	private class StoredFieldDummy {
		@SearchId
		private int id = 1;
		
		@SearchField @SearchFilter
		private String name = "JULIAN";
		
		@SearchFilter
		private Date filterValue = new Date(0);
		
		@SearchSort
		private double sortValue = 1.2d;		
	}
	
	@Test
	public void testStoredFieldIsStored() throws IndexObjectException, SearchEngineMappingException, SecurityException, NoSuchFieldException {
		
		StoredFieldDummy dummy = new StoredFieldDummy();
		
		Utils.configureAndMap(dummy);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
					
		SemiIndex semiIndex = pipeline.processObject(dummy);
		
		IndexObject dto =  new IndexObject(dummy);
		
		Map<Term, PostingMetadata> termPostingMap = semiIndex.getSemiIndexMap().get(dto);
		
		Field filterField = dummy.getClass().getDeclaredField("filterValue");
		
		Field sortField = dummy.getClass().getDeclaredField("sortValue");
		
		Assert.assertEquals(new Date(0), termPostingMap.get(new Term("JULIAN")).getStoredFieldValue(filterField));
		
		Assert.assertEquals(1.2d, termPostingMap.get(new Term("JULIAN")).getStoredFieldValue(sortField));
				
	}
	
	@SuppressWarnings("unused")
	@Indexable
	@TextProcessor(OneTermTextProcessor.class)
	private class TextProcessorDummy {
		@SearchId
		private int id = 1;
		
		@SearchField
		private String attribute1 = "WORKS";
		
		@SearchField @Stemming(stemType=StemType.FULL_STEM,strategy=EnglishSnowballStemmingStrategy.class)
		private String attribute2 = "WORKS";
		
		@SearchField
		@TextProcessor(DefaultObjectTextProcessor.class)
		@Stemming(stemType=StemType.FULL_STEM,strategy=EnglishSnowballStemmingStrategy.class)
		private String attribute3 = "WORKS";
		
		@SearchFilter
		private Date filterValue = new Date(0);
		
		@SearchSort
		private double sortValue = 1.2d;		
	}
	
	@Test
	public void testTextProcessorIsCorrectlySelected() throws IndexObjectException, SearchEngineMappingException, SecurityException, NoSuchFieldException {
		
		TextProcessorDummy dummy = new TextProcessorDummy();
		
		Utils.configureAndMap(dummy);

		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		SemiIndex semiIndex = pipeline.processObject(dummy);
		
		IndexObject dto =  new IndexObject(dummy);
		
		Map<Term, PostingMetadata> termPostingMap = semiIndex.getSemiIndexMap().get(dto);
		
		Field attribute1 = TextProcessorDummy.class.getDeclaredField("attribute1");
		Field attribute2 = TextProcessorDummy.class.getDeclaredField("attribute2");
		Field attribute3 = TextProcessorDummy.class.getDeclaredField("attribute3");
		
		Assert.assertEquals( 2, termPostingMap.get(new Term("WORKS")).getSourceFields().size() );
		Assert.assertTrue( termPostingMap.get(new Term("WORKS")).getSourceFields().contains(attribute1) );
		Assert.assertTrue( termPostingMap.get(new Term("WORKS")).getSourceFields().contains(attribute2) );
		
		Assert.assertEquals( 1, termPostingMap.get(new Term("WORK")).getSourceFields().size() );
		Assert.assertTrue( termPostingMap.get(new Term("WORK")).getSourceFields().contains(attribute3) );
	}
	
	@SuppressWarnings("unused")
	
	@Indexable(makeSubclassesIndexable=true, indexName="GRANDGRAND") @TextProcessor(NullProcessor.class)
	private class GrandGrandfather {
		@SearchId int id;
		@SearchField public String gf = "From grandgrandfather";
	}
	
	@SuppressWarnings("unused")
	
	@Indexable(makeSubclassesIndexable=true)
	private class Grandfather extends GrandGrandfather {
		@SearchId int id = 0;
		@SearchField public String g = "From grandfather";
	}
	
	@SuppressWarnings("unused")
	
	private class Father extends Grandfather {
		@SearchId int id = 0;
		@SearchField public String f = "From father";
	} 
	
	@SuppressWarnings("unused")
		
	private class Son extends Father {
		@SearchField public String s = "From son";		
	}

	@Test
	public void HierarchyIsIndexed() throws IndexObjectException, SearchEngineMappingException, SecurityException, NoSuchFieldException {
		
		Son son = new Son();
		
		Utils.configureAndMap(son);

		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		SemiIndex semiIndex = pipeline.processObject(son);
		
		IndexObject dto =  new IndexObject(son);
		
		Map<Term, PostingMetadata> termPostingMap = semiIndex.getSemiIndexMap().get(dto);

		Assert.assertEquals( 4, termPostingMap.size() );
		
		Field s = Son.class.getDeclaredField("s");
		Field f = Father.class.getDeclaredField("f");
		Field g = Grandfather.class.getDeclaredField("g");
		
		Collection<Field> sourceFields = termPostingMap.get(new Term("FROM")).getSourceFields();
		Assert.assertEquals( 3, sourceFields.size() );
				
		int field1Count = 0, field2Count = 0, field3Count = 0;
		Iterator<PostingMetadata> iterator = termPostingMap.values().iterator();		
		while(iterator.hasNext()) {
			PostingMetadata current = iterator.next();
			
			if(current.getSourceFields().contains(s)) field1Count++;
			if(current.getSourceFields().contains(f)) field2Count++;
			if(current.getSourceFields().contains(g)) field3Count++;
		}
		
		Assert.assertEquals(2, field1Count );
		Assert.assertEquals(2, field2Count );
		Assert.assertEquals(2, field3Count );
		
	}

	@IndexableContainer
	public class DummyContainter {
		@SearchCollection(reference=IndexReference.SELF) List<DummyContained> objectList = new ArrayList<DummyContained>();
	}
	
	@SuppressWarnings("unused")
	@Indexable
	public class DummyContained {
		@SearchId private final int id;
		@SearchField private final String attribute;
		
		public DummyContained(int id, String attribute) {
			this.id = id; this.attribute = attribute;
		}
	}
	
	@Test
	public void IndexableContainerIndexesContainedElementsReferencingThemselves() throws IndexObjectException, SearchEngineMappingException {
		
		DummyContained list1contained = new DummyContained(0,"One");
		DummyContained list2contained = new DummyContained(1,"Two");
		DummyContained list3contained = new DummyContained(2,"Three");
		
		DummyContainter container = new DummyContainter();
		container.objectList.add(list1contained);
		container.objectList.add(list2contained);
		container.objectList.add(list3contained);
				
		Utils.configureAndMap(list1contained);
		Utils.configureAndMap(list2contained);
		Utils.configureAndMap(list3contained);
		Utils.configureAndMap(container);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		SemiIndex semiIndex = pipeline.processObject(container);
		
		IndexObject dto =  new IndexObject(list1contained);		
		Map<Term, PostingMetadata> termPostingMap = semiIndex.getSemiIndexMap().get(dto);		
		Assert.assertEquals( 1, termPostingMap.size() );
		
		dto = new IndexObject(list2contained);		
		termPostingMap = semiIndex.getSemiIndexMap().get(dto);		
		Assert.assertEquals( 1, termPostingMap.size() );
		
		dto = new IndexObject(list3contained);		
		termPostingMap = semiIndex.getSemiIndexMap().get(dto);		
		Assert.assertEquals( 1, termPostingMap.size() );
		
		dto = new IndexObject(container, IndexObject.NO_ID);		
		Assert.assertNull(semiIndex.getSemiIndexMap().get(dto));
		
	}
	
	@Indexable @IndexableContainer 
	public class DummyContainterReferenced {
		@SearchId public int id = 0;
		@SearchCollection(reference=IndexReference.CONTAINER) List<DummyContained> objectList = new ArrayList<DummyContained>();
	}
	
	@Test
	public void IndexableContainerIndexesContainedElementsReferencingContainer() throws IndexObjectException, SearchEngineMappingException {
		
		DummyContained list1contained = new DummyContained(0,"One");
		DummyContained list2contained = new DummyContained(1,"Two");
		DummyContained list3contained = new DummyContained(2,"Three");
		
		DummyContainterReferenced container = new DummyContainterReferenced();
		container.objectList.add(list1contained);
		container.objectList.add(list2contained);
		container.objectList.add(list3contained);
				
		Utils.configureAndMap(list1contained);
		Utils.configureAndMap(list2contained);
		Utils.configureAndMap(list3contained);
		Utils.configureAndMap(container);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		SemiIndex semiIndex = pipeline.processObject(container);
		
		IndexObject dto =  new IndexObject(list1contained);		
		Map<Term, PostingMetadata> termPostingMap = semiIndex.getSemiIndexMap().get(dto);		
		Assert.assertNull(termPostingMap);
		
		dto = new IndexObject(list2contained);		
		termPostingMap = semiIndex.getSemiIndexMap().get(dto);		
		Assert.assertNull(termPostingMap);
		
		dto = new IndexObject(list3contained);		
		termPostingMap = semiIndex.getSemiIndexMap().get(dto);		
		Assert.assertNull(termPostingMap);
		
		dto = new IndexObject(container);
		termPostingMap = semiIndex.getSemiIndexMap().get(dto);
		Assert.assertEquals( 3, termPostingMap.size() );	
	}
	
	@Indexable @IndexableContainer 
	public class DummyContainterReferencesBoth {
		@SearchId public int id = 0;
		@SearchCollection(reference=IndexReference.BOTH) List<DummyContained> objectList = new ArrayList<DummyContained>();
	}
	
	@Test
	public void IndexableContainerIndexesContainedElementsReferencingBoth() throws IndexObjectException, SearchEngineMappingException {
		
		DummyContained list1contained = new DummyContained(0,"One");
		DummyContained list2contained = new DummyContained(1,"Two");
		DummyContained list3contained = new DummyContained(2,"Three");
		
		DummyContainterReferencesBoth container = new DummyContainterReferencesBoth();
		container.objectList.add(list1contained);
		container.objectList.add(list2contained);
		container.objectList.add(list3contained);
				
		Utils.configureAndMap(list1contained);
		Utils.configureAndMap(list2contained);
		Utils.configureAndMap(list3contained);
		Utils.configureAndMap(container);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		SemiIndex semiIndex = pipeline.processObject(container);
		
		IndexObject dto =  new IndexObject(list1contained);		
		Map<Term, PostingMetadata> termPostingMap = semiIndex.getSemiIndexMap().get(dto);		
		Assert.assertEquals( 1, termPostingMap.size() );
		
		dto = new IndexObject(list2contained);		
		termPostingMap = semiIndex.getSemiIndexMap().get(dto);		
		Assert.assertEquals( 1, termPostingMap.size() );
		
		dto = new IndexObject(list3contained);		
		termPostingMap = semiIndex.getSemiIndexMap().get(dto);		
		Assert.assertEquals( 1, termPostingMap.size() );
		
		dto = new IndexObject(container);
		termPostingMap = semiIndex.getSemiIndexMap().get(dto);
		Assert.assertEquals( 3, termPostingMap.size() );	
	}
	
	@Indexable @IndexableContainer
	public class DummyWithIndexableByMapping {		
		@SearchId int id = 0;
		@SearchContained(reference=IndexReference.SELF) SingleAttributeEntity containedSelfRef = new SingleAttributeEntity(1,"self");
		@SearchContained(reference=IndexReference.CONTAINER) SingleAttributeEntity containedContRef = new SingleAttributeEntity(2,"container");
		@SearchContained(reference=IndexReference.BOTH) SingleAttributeEntity containedContBoth = new SingleAttributeEntity(3,"both");
	}

	@Test
	public void SearchContainedIsIndexedWithFullMapping() throws IndexObjectException, SearchEngineMappingException {
		
		DummyWithIndexableByMapping entity = new DummyWithIndexableByMapping();
				
		AnnotationConfigurationMapper.configureAndMap(entity,true);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		SemiIndex semiIndex = pipeline.processObject(entity);
				
		Assert.assertEquals( 3, semiIndex.getSemiIndexMap().size() );
		
		int numberOfReferences = 0;
		
		for (Map.Entry<IndexObject, Map<Term, PostingMetadata>> entry : semiIndex.getSemiIndexMap().entrySet()) {			
			Class<?> currentEntryEntityClass = entry.getKey().getEntity().getClass();
			
			if(entry.getValue().containsKey(new Term("SELF"))) {
				Assert.assertEquals( SingleAttributeEntity.class , currentEntryEntityClass );
				Assert.assertEquals( 1, entry.getKey().getId() );
				numberOfReferences++;
			}
			
			if(entry.getValue().containsKey(new Term("CONTAINER"))) {
				Assert.assertEquals( DummyWithIndexableByMapping.class , currentEntryEntityClass );
				Assert.assertEquals( 0 , entry.getKey().getId() );
				numberOfReferences++;
			}
			
			if(entry.getValue().containsKey(new Term("BOTH"))) {
				
				if(entry.getKey().getEntity().equals(entity)) {
					Assert.assertEquals( DummyWithIndexableByMapping.class , currentEntryEntityClass );
					Assert.assertEquals( 0, entry.getKey().getId() );
					numberOfReferences++;
				} else {
					Assert.assertEquals( SingleAttributeEntity.class , currentEntryEntityClass );
					Assert.assertEquals( 3 , entry.getKey().getId() );
					numberOfReferences++;
				}
			}
		}
		
		Assert.assertEquals(4, numberOfReferences);
	}
	
	
	private static class HardcodedProvider implements StopWordProvider {
		@Override
		public void provideStopWords(Language language, StopWordCleaner cleaner) {
			Set<Term> stopWords = new HashSet<Term>();
			if(language.equals(new Language("es"))) {
				stopWords.add(new Term("De"));
			} else {
				stopWords.add(new Term("Is"));
				stopWords.add(new Term("Of"));
			}
			cleaner.setStopWords(language, stopWords);
		}
		
	}
	
	@SuppressWarnings("unused")
	@Indexable @LangId("es") @TextProcessor(value=DefaultObjectTextProcessor.class)
	private class DummyWithStopWords {
		@SearchId
		private int id = 1;

		@SearchField 
		private String notToClean = "nothing to do here";
		
		@SearchField @TextProcessor(value=DefaultObjectTextProcessor.class,	stopWordProvider=HardcodedProvider.class) @LangId("en")
		private String attribute = "This is full of stop words";
	}

	@Test
	public void StopWordsAreCleaned() throws SearchEngineMappingException, IndexObjectException {

		DummyWithStopWords entity = new DummyWithStopWords();
		
		AnnotationConfigurationMapper.configureAndMap(entity,true);
		
		DefaultIndexingPipeline pipeline = new DefaultIndexingPipeline();
		
		SemiIndex semiIndex = pipeline.processObject(entity);
		
		IndexObject dto =  new IndexObject(entity);
		
		Map<Term, PostingMetadata> termPostingMap = semiIndex.getSemiIndexMap().get(dto);

		Assert.assertTrue( termPostingMap.containsKey(new Term("This")) );
		Assert.assertFalse( termPostingMap.containsKey(new Term("is")) );
		Assert.assertTrue( termPostingMap.containsKey(new Term("full")) );
		Assert.assertFalse( termPostingMap.containsKey(new Term("of")) );
		Assert.assertTrue( termPostingMap.containsKey(new Term("stop")) );
		Assert.assertTrue( termPostingMap.containsKey(new Term("words")) );
	}
}
