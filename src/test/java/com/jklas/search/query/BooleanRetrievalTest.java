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
package com.jklas.search.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.jklas.search.annotations.IndexReference;
import com.jklas.search.annotations.Indexable;
import com.jklas.search.annotations.IndexableContainer;
import com.jklas.search.annotations.SearchCollection;
import com.jklas.search.annotations.SearchField;
import com.jklas.search.annotations.SearchId;
import com.jklas.search.configuration.AnnotationConfigurationMapper;
import com.jklas.search.engine.BooleanSearch;
import com.jklas.search.engine.Language;
import com.jklas.search.engine.dto.ObjectKeyResult;
import com.jklas.search.engine.processor.QueryTextProcessor;
import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.exception.SearchEngineException;
import com.jklas.search.exception.SearchEngineMappingException;
import com.jklas.search.index.Term;
import com.jklas.search.index.memory.MemoryIndex;
import com.jklas.search.index.memory.MemoryIndexReaderFactory;
import com.jklas.search.index.memory.MemoryIndexWriterFactory;
import com.jklas.search.indexer.DefaultIndexerService;
import com.jklas.search.indexer.pipeline.DefaultIndexingPipeline;
import com.jklas.search.query.bool.BooleanPostingListExtractor;
import com.jklas.search.query.bool.BooleanQuery;
import com.jklas.search.query.bool.BooleanQueryParser;
import com.jklas.search.query.operator.AndOperator;
import com.jklas.search.query.operator.MinusOperator;
import com.jklas.search.query.operator.OrOperator;
import com.jklas.search.query.operator.RetrieveOperator;
import com.jklas.search.util.TextLibrary;
import com.jklas.search.util.Utils;

public class BooleanRetrievalTest {

	private static BooleanPostingListExtractor extractor = new BooleanPostingListExtractor();

	@Test
	public void ZeroTermQueryThrowsException() {
		BooleanQueryParser parser = new BooleanQueryParser("");
		try {
			parser.getQuery();
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
			return;
		}

		Assert.fail();
	}

	@Test
	public void OneTermQueryIsParsedAsOneRetrieveOperator() {
		BooleanQueryParser parser = new BooleanQueryParser("search");
		BooleanQuery query = parser.getQuery();

		Assert.assertEquals(query.getRootOperator(),
				new RetrieveOperator<ObjectKeyResult>(new Term("search"),extractor));
	}

	@Test
	public void TwoTermAndQueryIsParsedOk() {
		BooleanQueryParser parser = new BooleanQueryParser("doing some");
		BooleanQuery query = parser.getQuery();

		RetrieveOperator<ObjectKeyResult> some = new RetrieveOperator<ObjectKeyResult>(new Term("some"),extractor);
		RetrieveOperator<ObjectKeyResult> doing = new RetrieveOperator<ObjectKeyResult>(new Term("doing"),extractor);

		AndOperator<ObjectKeyResult> and = new AndOperator<ObjectKeyResult>(doing, some);

		Assert.assertEquals(query.getRootOperator(), and);
	}

	@Test
	public void FiveTermQueryIsParsedAsFiveAndOperator() {
		BooleanQueryParser parser = new BooleanQueryParser("doing some object information retrieval");
		BooleanQuery query = parser.getQuery();

		RetrieveOperator<ObjectKeyResult> retrieval = new RetrieveOperator<ObjectKeyResult>(new Term("retrieval"),extractor);
		RetrieveOperator<ObjectKeyResult> information = new RetrieveOperator<ObjectKeyResult>(new Term("information"),extractor);
		RetrieveOperator<ObjectKeyResult> object = new RetrieveOperator<ObjectKeyResult>(new Term("object"),extractor);
		RetrieveOperator<ObjectKeyResult> some = new RetrieveOperator<ObjectKeyResult>(new Term("some"),extractor);
		RetrieveOperator<ObjectKeyResult> doing = new RetrieveOperator<ObjectKeyResult>(new Term("doing"),extractor);

		AndOperator<ObjectKeyResult> and45 = new AndOperator<ObjectKeyResult>(information, retrieval); 
		AndOperator<ObjectKeyResult> and345 = new AndOperator<ObjectKeyResult>(object, and45);
		AndOperator<ObjectKeyResult> and2345 = new AndOperator<ObjectKeyResult>(some, and345);
		AndOperator<ObjectKeyResult> and12345 = new AndOperator<ObjectKeyResult>(doing, and2345);

		Assert.assertEquals(query.getRootOperator(),
				and12345);
	}

	@Test
	public void OneAndOneOrParsing() {
		BooleanQueryParser parser = new BooleanQueryParser("search +OR find");
		BooleanQuery query = parser.getQuery();

		RetrieveOperator<ObjectKeyResult> search = new RetrieveOperator<ObjectKeyResult>(new Term("search"),extractor);
		RetrieveOperator<ObjectKeyResult> find= new RetrieveOperator<ObjectKeyResult>(new Term("find"),extractor);

		OrOperator<ObjectKeyResult> or = new OrOperator<ObjectKeyResult>(search,find);

		Assert.assertEquals(or,query.getRootOperator());
	}

	@Test
	public void NotParsing() {
		BooleanQueryParser parser = new BooleanQueryParser("these +NOT those");
		BooleanQuery query = parser.getQuery();

		RetrieveOperator<ObjectKeyResult> these = new RetrieveOperator<ObjectKeyResult>(new Term("these"),extractor);
		RetrieveOperator<ObjectKeyResult> those = new RetrieveOperator<ObjectKeyResult>(new Term("those"),extractor);

		MinusOperator<ObjectKeyResult> not = new MinusOperator<ObjectKeyResult>(these,those);

		Assert.assertEquals(not,query.getRootOperator());
	}

	@Test
	public void DoubleNotParsing() {
		BooleanQueryParser parser = new BooleanQueryParser("these +NOT those +NOT that");
		BooleanQuery query = parser.getQuery();

		RetrieveOperator<ObjectKeyResult> these = new RetrieveOperator<ObjectKeyResult>(new Term("these"),extractor);
		RetrieveOperator<ObjectKeyResult> those = new RetrieveOperator<ObjectKeyResult>(new Term("those"),extractor);
		RetrieveOperator<ObjectKeyResult> that = new RetrieveOperator<ObjectKeyResult>(new Term("that"),extractor);
		
		MinusOperator<ObjectKeyResult> notThat = new MinusOperator<ObjectKeyResult>(these,that);
		MinusOperator<ObjectKeyResult> notThose = new MinusOperator<ObjectKeyResult>(notThat, those);
		
		Assert.assertEquals(notThose,query.getRootOperator());
	}
	
	@Test
	public void ReverseDoubleNotParsing() {
		BooleanQueryParser parser = new BooleanQueryParser("these +NOT that +NOT those ");
		BooleanQuery query = parser.getQuery();

		RetrieveOperator<ObjectKeyResult> these = new RetrieveOperator<ObjectKeyResult>(new Term("these"),extractor);
		RetrieveOperator<ObjectKeyResult> those = new RetrieveOperator<ObjectKeyResult>(new Term("those"),extractor);
		RetrieveOperator<ObjectKeyResult> that = new RetrieveOperator<ObjectKeyResult>(new Term("that"),extractor);
		
		MinusOperator<ObjectKeyResult> notThose = new MinusOperator<ObjectKeyResult>(these,those);
		MinusOperator<ObjectKeyResult> notThat = new MinusOperator<ObjectKeyResult>(notThose, that);
		
		Assert.assertEquals(notThat,query.getRootOperator());
	}
	
	@Test
	public void TwoAndAndTwoOrIsParsedAsFourOperators() {
		BooleanQueryParser parser = new BooleanQueryParser("search +AND find +OR search +AND lookup");
		BooleanQuery query = parser.getQuery();

		RetrieveOperator<ObjectKeyResult> search1 = new RetrieveOperator<ObjectKeyResult>(new Term("search"),extractor);
		RetrieveOperator<ObjectKeyResult> find = new RetrieveOperator<ObjectKeyResult>(new Term("find"),extractor);
		RetrieveOperator<ObjectKeyResult> search2 = new RetrieveOperator<ObjectKeyResult>(new Term("search"),extractor);
		RetrieveOperator<ObjectKeyResult> lookup = new RetrieveOperator<ObjectKeyResult>(new Term("lookup"),extractor);		

		AndOperator<ObjectKeyResult> and1 = new AndOperator<ObjectKeyResult>(search1, find); 
		AndOperator<ObjectKeyResult> and2 = new AndOperator<ObjectKeyResult>(search2, lookup);
		OrOperator<ObjectKeyResult> or = new OrOperator<ObjectKeyResult>(and1, and2);

		Assert.assertEquals(or, query.getRootOperator());

	}

	@Test
	public void TwoAndAndTwoOrAndOneNotIsParsedAsFiveOperators() {
		BooleanQueryParser parser = new BooleanQueryParser("search +AND find +OR search +AND lookup +NOT browse");
		BooleanQuery query = parser.getQuery();

		RetrieveOperator<ObjectKeyResult> search1 = new RetrieveOperator<ObjectKeyResult>(new Term("search"),extractor);
		RetrieveOperator<ObjectKeyResult> find = new RetrieveOperator<ObjectKeyResult>(new Term("find"),extractor);
		RetrieveOperator<ObjectKeyResult> search2 = new RetrieveOperator<ObjectKeyResult>(new Term("search"),extractor);
		RetrieveOperator<ObjectKeyResult> lookup = new RetrieveOperator<ObjectKeyResult>(new Term("lookup"),extractor);		
		RetrieveOperator<ObjectKeyResult> browse = new RetrieveOperator<ObjectKeyResult>(new Term("browse"),extractor);		
		
		AndOperator<ObjectKeyResult> and1 = new AndOperator<ObjectKeyResult>(search1, find); 
		AndOperator<ObjectKeyResult> and2 = new AndOperator<ObjectKeyResult>(search2, lookup);
		OrOperator<ObjectKeyResult> or = new OrOperator<ObjectKeyResult>(and1, and2);
		MinusOperator<ObjectKeyResult> minus = new MinusOperator<ObjectKeyResult>(or, browse);
		
		Assert.assertEquals(minus, query.getRootOperator());

	}

	
	@Test
	public void AndAtTheBeginningIsOptional() {
		BooleanQueryParser parser = new BooleanQueryParser("+AND find");
		BooleanQuery queryWithExplicitAnd = parser.getQuery();

		RetrieveOperator<ObjectKeyResult> find = new RetrieveOperator<ObjectKeyResult>(new Term("find"),extractor);		

		Assert.assertEquals(find, queryWithExplicitAnd.getRootOperator());

	}
	
	@Test
	public void IllegalQueriesThrowsException() {
		try {
			new BooleanQueryParser("+NOT").getQuery();			
			Assert.fail();
		} catch(IllegalArgumentException iau) {
			Assert.assertTrue(true);
		}
		
		try {
			new BooleanQueryParser("+OR").getQuery();			
			Assert.fail();
		} catch(IllegalArgumentException iau) {
			Assert.assertTrue(true);
		}

		try {
			new BooleanQueryParser("+AND").getQuery();			
			Assert.fail();
		} catch(IllegalArgumentException iau) {
			Assert.assertTrue(true);
		}

		try {
			new BooleanQueryParser("+NOT +NOT").getQuery();			
			Assert.fail();
		} catch(IllegalArgumentException iau) {
			Assert.assertTrue(true);
		}
		
		try {
			new BooleanQueryParser("+OR +OR").getQuery();			
			Assert.fail();
		} catch(IllegalArgumentException iau) {
			Assert.assertTrue(true);
		}
		
		try {
			new BooleanQueryParser("+AND +AND").getQuery();			
			Assert.fail();
		} catch(IllegalArgumentException iau) {
			Assert.assertTrue(true);
		}

		try {
			new BooleanQueryParser("+AND +NOT search").getQuery();			
			Assert.fail();
		} catch(IllegalArgumentException iau) {
			Assert.assertTrue(true);
		}
		
		try {
			new BooleanQueryParser("+AND +OR search").getQuery();			
			Assert.fail();
		} catch(IllegalArgumentException iau) {
			Assert.assertTrue(true);
		}
	
		try {
			new BooleanQueryParser("+NOT +OR search").getQuery();			
			Assert.fail();
		} catch(IllegalArgumentException iau) {
			Assert.assertTrue(true);
		}
		
		try {
			new BooleanQueryParser("+AND +AND search").getQuery();			
			Assert.fail();
		} catch(IllegalArgumentException iau) {
			Assert.assertTrue(true);
		}

		try {
			new BooleanQueryParser("search +NOT").getQuery();			
			Assert.fail();
		} catch(IllegalArgumentException iau) {
			Assert.assertTrue(true);
		}

		try {
			new BooleanQueryParser("search +OR").getQuery();			
			Assert.fail();
		} catch(IllegalArgumentException iau) {
			Assert.assertTrue(true);
		}
		
		try {
			new BooleanQueryParser("search +AND").getQuery();			
			Assert.fail();
		} catch(IllegalArgumentException iau) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void ZeroObjectsOnIndexDoesntRetrivesAnything() {
		MemoryIndex.newDefaultIndex();

		BooleanQueryParser parser = new BooleanQueryParser("foo");
		BooleanQuery query = parser.getQuery();

		BooleanSearch booleanSearch = new BooleanSearch(query,MemoryIndexReaderFactory.getInstance());
		Set<ObjectKeyResult> result = booleanSearch.search();
		Assert.assertEquals(0, result.size() );
	}

	@Test
	public void OneTermQueryRetrievesObjectWithThatTerm() {
		Utils.SingleAttributeEntity ipod    = new Utils.SingleAttributeEntity(0,"ipod touch 16gb");

		Utils.setupSampleMemoryIndex(ipod);

		BooleanQueryParser parser = new BooleanQueryParser("ipod");
		BooleanQuery query = parser.getQuery();

		BooleanSearch booleanSearch = new BooleanSearch(query,MemoryIndexReaderFactory.getInstance());
		Set<ObjectKeyResult> result = booleanSearch.search();
		Assert.assertEquals(1, result.size() );
	}

	@Test
	public void OneTermQueryRetrievesTwoObjectsWithThatTerm() {
		Utils.SingleAttributeEntity ipod    = new Utils.SingleAttributeEntity(0,"ipod touch 16gb");
		Utils.SingleAttributeEntity iphone  = new Utils.SingleAttributeEntity(1,"iphone 3gs 16gb");

		Utils.setupSampleMemoryIndex(ipod, iphone);

		BooleanQueryParser parser = new BooleanQueryParser("16gb");
		BooleanQuery query = parser.getQuery();

		BooleanSearch booleanSearch = new BooleanSearch(query,MemoryIndexReaderFactory.getInstance());
		Set<ObjectKeyResult> result = booleanSearch.search();
		Assert.assertEquals(2, result.size() );
	}

	@Test
	public void TwoTermQueryRetrievesObjectWithBothTerms() {
		Utils.SingleAttributeEntity ipod    = new Utils.SingleAttributeEntity(0,"ipod touch 16gb");
		Utils.SingleAttributeEntity mp3    = new Utils.SingleAttributeEntity(1,"mp5 16gb");

		Utils.setupSampleMemoryIndex(ipod,mp3);

		BooleanQueryParser parser = new BooleanQueryParser("ipod touch");
		BooleanQuery query = parser.getQuery();

		BooleanSearch booleanSearch = new BooleanSearch(query,MemoryIndexReaderFactory.getInstance());
		Set<ObjectKeyResult> result = booleanSearch.search();
		Assert.assertEquals(1, result.size() );
	}

	@Test
	public void TermIsSuppressedByNot() {
		Utils.SingleAttributeEntity ipod    = new Utils.SingleAttributeEntity(0,"ipod touch 16gb");
		Utils.SingleAttributeEntity mp3    = new Utils.SingleAttributeEntity(1,"mp5 16gb");

		Utils.setupSampleMemoryIndex(ipod,mp3);

		BooleanQueryParser parser = new BooleanQueryParser("16gb +NOT 16gb");
		BooleanQuery query = parser.getQuery();

		BooleanSearch booleanSearch = new BooleanSearch(query,MemoryIndexReaderFactory.getInstance());
		Set<ObjectKeyResult> result = booleanSearch.search();
		Assert.assertEquals(0, result.size() );
	}
	
	@Test
	public void NotIsHonoredForTwoTermQuery() {
		Utils.SingleAttributeEntity ipod    = new Utils.SingleAttributeEntity(0,"ipod touch 16gb");
		Utils.SingleAttributeEntity mp3    = new Utils.SingleAttributeEntity(1,"mp5 16gb");

		Utils.setupSampleMemoryIndex(ipod,mp3);

		BooleanQueryParser parser = new BooleanQueryParser("16gb +NOT touch");
		BooleanQuery query = parser.getQuery();

		BooleanSearch booleanSearch = new BooleanSearch(query,MemoryIndexReaderFactory.getInstance());
		Set<ObjectKeyResult> result = booleanSearch.search();
		Assert.assertEquals(1, result.size() );
	}
	
	@Test
	public void TwoTermQueryDoesntRetrieveObjectsWithOnlyOneMatch() {
		Utils.SingleAttributeEntity touch16gb    = new Utils.SingleAttributeEntity(0,"touch 16gb");
		Utils.SingleAttributeEntity ipod16gb    = new Utils.SingleAttributeEntity(1,"ipod 16gb");

		Utils.setupSampleMemoryIndex(touch16gb,ipod16gb);

		BooleanQueryParser parser = new BooleanQueryParser("ipod touch");
		BooleanQuery query = parser.getQuery();

		BooleanSearch booleanSearch = new BooleanSearch(query,MemoryIndexReaderFactory.getInstance());
		Set<ObjectKeyResult> result = booleanSearch.search();
		Assert.assertEquals(0, result.size() );
	}

	@Test
	public void ExplicitAndRetrievesTheSameAsImplicitAnd() {
		Utils.SingleAttributeEntity touch16gb    = new Utils.SingleAttributeEntity(0,"touch 16gb");
		Utils.SingleAttributeEntity ipod16gb    = new Utils.SingleAttributeEntity(1,"ipod 16gb");

		Utils.setupSampleMemoryIndex(touch16gb,ipod16gb);

		BooleanQueryParser parser = new BooleanQueryParser("ipod +AND touch");
		BooleanQuery query = parser.getQuery();

		BooleanSearch booleanSearch = new BooleanSearch(query,MemoryIndexReaderFactory.getInstance());
		Set<ObjectKeyResult> resultWithExplicitAnd = booleanSearch.search();

		parser = new BooleanQueryParser("ipod touch");
		query = parser.getQuery();

		booleanSearch = new BooleanSearch(query,MemoryIndexReaderFactory.getInstance());
		Set<ObjectKeyResult> resultWithImplicitAnd = booleanSearch.search();

		Assert.assertEquals( resultWithExplicitAnd, resultWithImplicitAnd );
	}

	@Test
	public void MatchingEntitiesOnDifferentIndexesAreNotRetrieved() {
		MemoryIndex.newDefaultIndex();

		Utils.SingleAttributeEntity touch16gb    = new Utils.SingleAttributeEntity(0,"touch 16gb");
		Utils.SingleAttributeEntity ipod16gb    = new Utils.SingleAttributeEntity(1,"ipod 16gb");

		try {
			AnnotationConfigurationMapper.configureAndMap(touch16gb);
			AnnotationConfigurationMapper.configureAndMap(ipod16gb);
		} catch (SearchEngineMappingException e) {
			Assert.fail();
			throw new RuntimeException("this shouldn't happened.. mapping failed!",e);
		}
		
		DefaultIndexerService dis = new DefaultIndexerService(new DefaultIndexingPipeline(),MemoryIndexWriterFactory.getInstance());

		try {
			dis.create(touch16gb);
			dis.create(ipod16gb);				
		} catch (IndexObjectException e) {
			Assert.fail();
			throw new RuntimeException("this shouldn't happened.. can't construct IndexObjectDto",e);
		}

		BooleanQueryParser parser = new BooleanQueryParser("ipod +AND touch");
		BooleanQuery query = parser.getQuery();

		BooleanSearch booleanSearch = new BooleanSearch(query,MemoryIndexReaderFactory.getInstance());
		Set<ObjectKeyResult> resultWithExplicitAnd = booleanSearch.search();

		parser = new BooleanQueryParser("ipod touch");
		query = parser.getQuery();

		booleanSearch = new BooleanSearch(query,MemoryIndexReaderFactory.getInstance());
		Set<ObjectKeyResult> resultWithImplicitAnd = booleanSearch.search();

		Assert.assertEquals( resultWithExplicitAnd, resultWithImplicitAnd );
	}

	@Test
	public void OrQueryRetrievesAllObjectsWithTerm() {
		Utils.SingleAttributeEntity touch16gb    = new Utils.SingleAttributeEntity(0,"touch 16gb");
		Utils.SingleAttributeEntity ipod16gb    = new Utils.SingleAttributeEntity(1,"ipod 16gb");

		Utils.setupSampleMemoryIndex(touch16gb,ipod16gb);

		BooleanQueryParser parser = new BooleanQueryParser("ipod +OR touch");
		BooleanQuery query = parser.getQuery();

		BooleanSearch booleanSearch = new BooleanSearch(query,MemoryIndexReaderFactory.getInstance());
		Set<ObjectKeyResult> result = booleanSearch.search();

		Assert.assertEquals( 2, result.size() );
	}

	@Test
	public void NonexistentTermDoesntAffectOrResults() {
		Utils.SingleAttributeEntity touch16gb    = new Utils.SingleAttributeEntity(0,"touch 16gb");
		Utils.SingleAttributeEntity ipod16gb    = new Utils.SingleAttributeEntity(1,"ipod 16gb");
		Utils.SingleAttributeEntity lcd    = new Utils.SingleAttributeEntity(1,"lcd 32\"");

		Utils.setupSampleMemoryIndex(touch16gb,ipod16gb,lcd);

		BooleanQueryParser parser = new BooleanQueryParser("ipod +OR touch +OR foo");
		BooleanQuery query = parser.getQuery();

		BooleanSearch booleanSearch = new BooleanSearch(query,MemoryIndexReaderFactory.getInstance());
		Set<ObjectKeyResult> result = booleanSearch.search();

		Assert.assertEquals( 2, result.size() );
	}

	@Test
	public void PageSizeEstablishesCorrectWindow() {
		Object[] entities = new Object[90];

		for (int i = 1; i <= 90; i++) {
			entities[i-1] = new Utils.SingleAttributeEntity(i,"iPod Touch 16GB - U$S "+ (i*1.5d)); 
		}

		Utils.setupSampleMemoryIndex(entities);

		for (int i = 1; i <= 3; i++) {
			BooleanQueryParser parser = new BooleanQueryParser("ipod");
			BooleanQuery query = parser.getQuery();
			query.setPage(i);
			query.setPageSize(30);

			BooleanSearch booleanSearch = new BooleanSearch(query,MemoryIndexReaderFactory.getInstance());
			Set<ObjectKeyResult> result = booleanSearch.search();

			Assert.assertEquals(30, result.size() );			
		}
	}

	@Test
	public void PageSizeNotMultipleOfResultsWorksOk() {
		Object[] entities = new Object[50];

		for (int i = 1; i <= 50; i++) {
			entities[i-1] = new Utils.SingleAttributeEntity(i,"iPod Touch 16GB - U$S "+ (i*1.5d)); 
		}

		Utils.setupSampleMemoryIndex(entities);

		{
			BooleanQueryParser parser = new BooleanQueryParser("ipod");
			BooleanQuery query = parser.getQuery();
			query.setPage(1);
			query.setPageSize(30);

			BooleanSearch booleanSearch = new BooleanSearch(query,MemoryIndexReaderFactory.getInstance());
			Set<ObjectKeyResult> result = booleanSearch.search();

			Assert.assertEquals(30, result.size() );
		}

		{
			BooleanQueryParser parser = new BooleanQueryParser("ipod");
			BooleanQuery query = parser.getQuery();
			query.setPage(2);
			query.setPageSize(30);

			BooleanSearch booleanSearch = new BooleanSearch(query,MemoryIndexReaderFactory.getInstance());
			Set<ObjectKeyResult> result = booleanSearch.search();

			Assert.assertEquals(20, result.size() );
		}
		
		{
			BooleanQueryParser parser = new BooleanQueryParser("ipod");
			BooleanQuery query = parser.getQuery();
			query.setPage(3);
			query.setPageSize(30);

			BooleanSearch booleanSearch = new BooleanSearch(query,MemoryIndexReaderFactory.getInstance());
			Set<ObjectKeyResult> result = booleanSearch.search();

			Assert.assertEquals(0, result.size() );
		}
	}

	private class NoAppleTextProcessor implements QueryTextProcessor {
		@Override
		public List<Term> processText(String text, Language language) {			
			List<Term> terms = TextLibrary.tokenize(text.toUpperCase());
			terms.remove(new Term("APPLE"));			
			return terms;
		}
	}
	
	@Test
	public void CustomProcessor() {
		Utils.SingleAttributeEntity touch16gb    = new Utils.SingleAttributeEntity(0,"ipod touch 32gb");
		Utils.SingleAttributeEntity ipod16gb    = new Utils.SingleAttributeEntity(1,"apple 16gb");

		Utils.setupSampleMemoryIndex(touch16gb,ipod16gb);

		BooleanQueryParser parser = new BooleanQueryParser("ipod +OR apple touch", new NoAppleTextProcessor());
		
		BooleanSearch booleanSearch = new BooleanSearch(parser.getQuery(),MemoryIndexReaderFactory.getInstance());
		Set<ObjectKeyResult> result = booleanSearch.search();

		Assert.assertEquals( 1, result.size() );
		for (ObjectKeyResult objectKeyResult : result) {
			Assert.assertEquals( 0, objectKeyResult.getKey().getId() );
		}
	}

	@Indexable
	@IndexableContainer
	public class DummyContainterAndIndexable {
		@SearchCollection(reference=IndexReference.BOTH) List<Object> bothObjectList = new ArrayList<Object>();
		@SearchCollection(reference=IndexReference.CONTAINER) List<Object> containerObjectList = new ArrayList<Object>();
		@SearchCollection(reference=IndexReference.SELF) List<Object> selfObjectList = new ArrayList<Object>();
		@SearchField String a = "Container";
		@SearchId int id = 0;
	}
	
	@Test
	public void IndexableContainerIsRetrievedByItsContainedElements() throws SearchEngineMappingException, SearchEngineException, IllegalArgumentException, IllegalAccessException {
		DummyContainterAndIndexable dc = new DummyContainterAndIndexable();
		dc.bothObjectList.add(new Utils.SingleAttributeEntity(0,"both 0"));
		dc.bothObjectList.add(new Utils.SingleAttributeEntity(1,"both 1"));
		dc.bothObjectList.add(new Utils.SingleAttributeEntity(2,"both 2"));
		
		dc.containerObjectList.add(new Utils.SingleAttributeEntity(0,"container 0"));
		dc.containerObjectList.add(new Utils.SingleAttributeEntity(1,"container 1"));
		dc.containerObjectList.add(new Utils.SingleAttributeEntity(2,"container 2"));
		
		dc.selfObjectList.add(new Utils.SingleAttributeEntity(0,"self 0"));
		dc.selfObjectList.add(new Utils.SingleAttributeEntity(1,"self 1"));
		dc.selfObjectList.add(new Utils.SingleAttributeEntity(2,"self 2"));
		
		Utils.setupSampleMemoryIndex(dc);
		
		Set<ObjectKeyResult> result = new BooleanSearch(new BooleanQueryParser("both").getQuery(),MemoryIndexReaderFactory.getInstance()).search();
		Assert.assertEquals(4, result.size() );			

		result = new BooleanSearch(new BooleanQueryParser("container").getQuery(),MemoryIndexReaderFactory.getInstance()).search();
		Assert.assertEquals(1, result.size() );			
		
		result = new BooleanSearch(new BooleanQueryParser("self").getQuery(),MemoryIndexReaderFactory.getInstance()).search();
		Assert.assertEquals(3, result.size() );			
	}

}
