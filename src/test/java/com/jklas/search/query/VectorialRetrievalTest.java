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
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.jklas.search.annotations.IndexReference;
import com.jklas.search.annotations.Indexable;
import com.jklas.search.annotations.IndexableContainer;
import com.jklas.search.annotations.SearchCollection;
import com.jklas.search.annotations.SearchField;
import com.jklas.search.annotations.SearchId;
import com.jklas.search.engine.Language;
import com.jklas.search.engine.VectorSearch;
import com.jklas.search.engine.dto.SingleTermObjectResult;
import com.jklas.search.engine.dto.VectorRankedResult;
import com.jklas.search.engine.processor.DefaultQueryTextProcessor;
import com.jklas.search.engine.processor.QueryTextProcessor;
import com.jklas.search.engine.stemming.SpanishSnowballStemmingStrategy;
import com.jklas.search.engine.stemming.StemType;
import com.jklas.search.engine.stemming.snowball.SpanishStemmer;
import com.jklas.search.exception.SearchEngineException;
import com.jklas.search.exception.SearchEngineMappingException;
import com.jklas.search.index.Term;
import com.jklas.search.index.memory.MemoryIndex;
import com.jklas.search.index.memory.MemoryIndexReader;
import com.jklas.search.query.operator.OrOperator;
import com.jklas.search.query.operator.RetrieveOperator;
import com.jklas.search.query.vectorial.VectorPostingListExtractor;
import com.jklas.search.query.vectorial.VectorQuery;
import com.jklas.search.query.vectorial.VectorQueryParser;
import com.jklas.search.util.TextLibrary;
import com.jklas.search.util.Utils;
import com.jklas.search.util.Utils.SingleAttributeEntity;

public class VectorialRetrievalTest {

	private static VectorPostingListExtractor extractor = new VectorPostingListExtractor();

	// -- QUERY PARSING

	@Test
	public void ZeroTermQueryThrowsException() {
		VectorQueryParser parser = new VectorQueryParser("");

		try {
			parser.getQuery();
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
			return;
		}

		Assert.fail();
	}

	@Test
	public void OneTermQueryIsParsedAsRetrieveOperator() {
		VectorQueryParser parser = new VectorQueryParser("search");
		VectorQuery query = parser.getQuery();

		Assert.assertEquals(query.getRootOperator(),
				new RetrieveOperator<SingleTermObjectResult>(new Term("search"),extractor));
	}

	@Test
	public void AndTermIsParsedAsTerm() {
		VectorQueryParser parser = new VectorQueryParser("+AND");
		VectorQuery query = parser.getQuery();

		Assert.assertEquals(query.getRootOperator(),
				new RetrieveOperator<SingleTermObjectResult>(new Term("+AND"),extractor));
	}

	@Test
	public void AndInLowercaseTermIsParsedAsTerm() {
		VectorQueryParser parser = new VectorQueryParser("+and");
		VectorQuery query = parser.getQuery();

		Assert.assertEquals(query.getRootOperator(),
				new RetrieveOperator<SingleTermObjectResult>(new Term("+AND"),extractor));
	}

	@Test
	public void TwoTermAndQueryIsParsedOk() {
		VectorQueryParser parser = new VectorQueryParser("doing some");
		VectorQuery query = parser.getQuery();

		RetrieveOperator<SingleTermObjectResult> some = new RetrieveOperator<SingleTermObjectResult>(new Term("some"),extractor);
		RetrieveOperator<SingleTermObjectResult> doing = new RetrieveOperator<SingleTermObjectResult>(new Term("doing"),extractor);

		OrOperator<SingleTermObjectResult> or = new OrOperator<SingleTermObjectResult>(doing, some);

		Assert.assertEquals(query.getRootOperator(), or);
	}

	@Test
	public void FiveTermQueryIsParsedAsFiveOrOperator() {
		VectorQueryParser parser = new VectorQueryParser("doing some object information retrieval");
		VectorQuery query = parser.getQuery();

		RetrieveOperator<SingleTermObjectResult> retrieval = new RetrieveOperator<SingleTermObjectResult>(new Term("RETRIEVAL"),extractor);
		RetrieveOperator<SingleTermObjectResult> information = new RetrieveOperator<SingleTermObjectResult>(new Term("INFORMATION"),extractor);
		RetrieveOperator<SingleTermObjectResult> object = new RetrieveOperator<SingleTermObjectResult>(new Term("OBJECT"),extractor);
		RetrieveOperator<SingleTermObjectResult> some = new RetrieveOperator<SingleTermObjectResult>(new Term("SOME"),extractor);
		RetrieveOperator<SingleTermObjectResult> doing = new RetrieveOperator<SingleTermObjectResult>(new Term("DOING"),extractor);

		OrOperator<SingleTermObjectResult> or45 = new OrOperator<SingleTermObjectResult>(information, retrieval); 
		OrOperator<SingleTermObjectResult> or345 = new OrOperator<SingleTermObjectResult>(object, or45);
		OrOperator<SingleTermObjectResult> or2345 = new OrOperator<SingleTermObjectResult>(some, or345);
		OrOperator<SingleTermObjectResult> or12345 = new OrOperator<SingleTermObjectResult>(doing, or2345);

		Assert.assertEquals(query.getRootOperator(),
				or12345);
	}

	@Test
	public void QueryWithFiveUnrepeatedWordsTermFrequencyTest() {
		VectorQueryParser parser = new VectorQueryParser("doing some object information retrieval");
		VectorQuery query = parser.getQuery();

		Map<Term,Integer> termVectors = query.getTermVectors();

		Assert.assertEquals(new Integer(1), termVectors.get(new Term("doing".toUpperCase())));
		Assert.assertEquals(new Integer(1), termVectors.get(new Term("some".toUpperCase())));
		Assert.assertEquals(new Integer(1), termVectors.get(new Term("object".toUpperCase())));
		Assert.assertEquals(new Integer(1), termVectors.get(new Term("information".toUpperCase())));
		Assert.assertEquals(new Integer(1), termVectors.get(new Term("retrieval".toUpperCase())));

	}

	@Test
	public void QuerySameTermFiveTimesWordsTermFrequencyTest() {
		VectorQueryParser parser = new VectorQueryParser("five five five five five");
		VectorQuery query = parser.getQuery();

		Map<Term,Integer> termVectors = query.getTermVectors();

		Assert.assertEquals(new Integer(5), termVectors.get(new Term("five".toUpperCase())));		
	}

	@Test
	public void RedundantTermsAreDeleted() {
		VectorQueryParser parser = new VectorQueryParser("five five five five five");
		VectorQuery query = parser.getQuery();

		Assert.assertTrue(query.getRootOperator() instanceof RetrieveOperator<?>);		
	}

	@Test
	public void StemmingIsApplied() {
		
		String word = "palabras";
		
		DefaultQueryTextProcessor textProcessor = new DefaultQueryTextProcessor();
		
		textProcessor.setStemmerStrategy(new SpanishSnowballStemmingStrategy() );
		textProcessor.setStemType(StemType.FULL_STEM);
		
		VectorQueryParser parser = new VectorQueryParser(word, textProcessor);
		
		VectorQuery query = parser.getQuery();		
		
		Term term = new Term(word);
		Term stemmedWord = new SpanishStemmer().stem(term);
		
		Assert.assertTrue(query.getTerms().contains(stemmedWord));
	}

	// -- RETRIEVAL

	@Test
	public void ZeroObjectsOnIndexDoesntRetrivesAnything() {
		MemoryIndex.renewAllIndexes();

		VectorQueryParser parser = new VectorQueryParser("foo");
		VectorQuery query = parser.getQuery();

		VectorSearch vectorSearch = new VectorSearch(query, new MemoryIndexReader());
		List<VectorRankedResult> result = vectorSearch.search();
		Assert.assertEquals(0, result.size() );
	}

	@Test
	public void OneTermQuerysearchsObjectWithThatTerm() {
		SingleAttributeEntity ipod    = new SingleAttributeEntity(0,"ipod touch 16gb");

		Utils.setupSampleMemoryIndex(ipod);

		VectorQueryParser parser = new VectorQueryParser("ipod");
		VectorQuery query = parser.getQuery();

		VectorSearch vectorSearch = new VectorSearch(query, new MemoryIndexReader());
		List<VectorRankedResult> result = vectorSearch.search();

		Assert.assertEquals(1, result.size() );
	}

	@Test	
	public void OneTermQuerysearchsTwoObjectsWithThatTerm() {
		SingleAttributeEntity ipod    = new SingleAttributeEntity(0,"ipod touch 16gb");
		SingleAttributeEntity iphone  = new SingleAttributeEntity(1,"iphone 3gs 16gb");

		Utils.setupSampleMemoryIndex(ipod, iphone);

		VectorQuery query = new VectorQueryParser("16gb").getQuery();

		List<VectorRankedResult> result = new VectorSearch(query, new MemoryIndexReader()).search();

		Assert.assertEquals(2, result.size() );
	}

	@Test	
	public void TwoTermQuerysearchsAnyAndOnlyObjectsWithThatTerm() {
		SingleAttributeEntity ipod    = new SingleAttributeEntity(0,"apple ipod");
		SingleAttributeEntity iphone  = new SingleAttributeEntity(1,"apple iphone");
		SingleAttributeEntity zune  = new SingleAttributeEntity(2,"microsoft zune");

		Utils.setupSampleMemoryIndex(ipod, iphone, zune);

		VectorQuery query = new VectorQueryParser("apple").getQuery();

		List<VectorRankedResult> result = new VectorSearch(query, new MemoryIndexReader()).search();

		Assert.assertEquals(2, result.size() );

		for (VectorRankedResult vectorResult : result) {
			if((Integer)vectorResult.getKey().getId() == 1) {
				Assert.assertEquals(SingleAttributeEntity.class , vectorResult.getKey().getClazz() );				
			} else if((Integer)vectorResult.getKey().getId() == 0) {
				Assert.assertEquals(SingleAttributeEntity.class , vectorResult.getKey().getClazz() );				
			} else Assert.fail();

		}
	}

	@Test	
	public void TwoTermQueryHasImplicitOr() {
		SingleAttributeEntity ipod    = new SingleAttributeEntity(0,"apple ipod");
		SingleAttributeEntity iphone  = new SingleAttributeEntity(1,"apple iphone");
		SingleAttributeEntity zune  = new SingleAttributeEntity(2,"microsoft zune");

		Utils.setupSampleMemoryIndex(ipod, iphone, zune);

		VectorQuery query = new VectorQueryParser("zune apple").getQuery();

		List<VectorRankedResult> result = new VectorSearch(query, new MemoryIndexReader()).search();

		Assert.assertEquals(3, result.size() );

		for (VectorRankedResult vectorResult : result) {
			if((Integer)vectorResult.getKey().getId() == 0) {
				Assert.assertEquals(SingleAttributeEntity.class , vectorResult.getKey().getClazz() );				
			} else if((Integer)vectorResult.getKey().getId() == 1) {
				Assert.assertEquals(SingleAttributeEntity.class , vectorResult.getKey().getClazz() );				
			} else if((Integer)vectorResult.getKey().getId() == 2) {
				Assert.assertEquals(SingleAttributeEntity.class , vectorResult.getKey().getClazz() );
			} else
				Assert.fail();
		}
	}

	@Test	
	public void MultiplePostingsArePreserved() {
		SingleAttributeEntity ipod    = new SingleAttributeEntity(0,"apple apple");
		SingleAttributeEntity iphone  = new SingleAttributeEntity(1,"apple iphone");
		SingleAttributeEntity zune  = new SingleAttributeEntity(2,"microsoft zune");

		Utils.setupSampleMemoryIndex(ipod, iphone, zune);

		VectorQuery query = new VectorQueryParser("zune apple").getQuery();

		List<VectorRankedResult> result = new VectorSearch(query, new MemoryIndexReader()).search();

		Assert.assertEquals(3, result.size() );

		for (VectorRankedResult vectorResult : result) {
			if((Integer)vectorResult.getKey().getId() == 0) {
				Assert.assertEquals(SingleAttributeEntity.class , vectorResult.getKey().getClazz() );				
			} else if((Integer)vectorResult.getKey().getId() == 1) {
				Assert.assertEquals(SingleAttributeEntity.class , vectorResult.getKey().getClazz() );				
			} else if((Integer)vectorResult.getKey().getId() == 2) {
				Assert.assertEquals(SingleAttributeEntity.class , vectorResult.getKey().getClazz() );
			} else
				Assert.fail();
		}
	}

	@Test
	public void VectorPageSizeEstablishesCorrectWindow() {
		Object[] entities = new Object[180];

		for (int i = 1; i <= 180; i++) {
			entities[i-1] = new SingleAttributeEntity(i,"iPod Touch 16GB - U$S "+ (i*1.5d)); 
		}

		Utils.setupSampleMemoryIndex(entities);

		for (int i = 1; i <= 6; i++) {
			VectorQueryParser parser = new VectorQueryParser("ipod");
			VectorQuery query = parser.getQuery();
			query.setPage(i);
			query.setPageSize(30);

			VectorSearch vectorSearch = new VectorSearch(query, new MemoryIndexReader());
			List<VectorRankedResult> result = vectorSearch.search();

			Assert.assertEquals(30, result.size() );			
		}
	}

	@Test
	public void VectorPageSizeNotMultipleOfResultsWorksOk() {
		Object[] entities = new Object[50];

		for (int i = 1; i <= 50; i++) {
			entities[i-1] = new SingleAttributeEntity(i,"iPod Touch 16GB - U$S "+ (i*1.5d)); 
		}

		Utils.setupSampleMemoryIndex(entities);

		{
			VectorQueryParser parser = new VectorQueryParser("ipod");
			VectorQuery query = parser.getQuery();
			query.setPage(1);
			query.setPageSize(30);

			VectorSearch  vectorSearch = new VectorSearch(query, new MemoryIndexReader());
			List<VectorRankedResult> result = vectorSearch.search();

			Assert.assertEquals(30, result.size() );
		}

		{
			VectorQueryParser parser = new VectorQueryParser("ipod");
			VectorQuery query = parser.getQuery();
			query.setPage(2);
			query.setPageSize(30);

			VectorSearch  vectorSearch = new VectorSearch(query, new MemoryIndexReader());
			List<VectorRankedResult> result = vectorSearch.search();

			Assert.assertEquals(20, result.size() );		}

		{
			VectorQueryParser parser = new VectorQueryParser("ipod");
			VectorQuery query = parser.getQuery();
			query.setPage(3);
			query.setPageSize(30);

			VectorSearch  vectorSearch = new VectorSearch(query, new MemoryIndexReader());
			List<VectorRankedResult> result = vectorSearch.search();

			Assert.assertEquals(0, result.size() );
		}
	}

	private class NoTouchTextProcessor implements QueryTextProcessor {
		@Override
		public List<Term> processText(String text, Language language) {
			List<Term> queryTokens = TextLibrary.tokenize(text.toUpperCase());
			queryTokens.remove(new Term("TOUCH"));
			return queryTokens;
		}
	}

	@Test
	public void CustomProcessor() {
		SingleAttributeEntity touch16gb   = new SingleAttributeEntity(0,"ipod touch 32gb");
		SingleAttributeEntity ipod16gb    = new SingleAttributeEntity(1,"touch 16gb");

		Utils.setupSampleMemoryIndex(touch16gb,ipod16gb);

		VectorQueryParser parser = new VectorQueryParser("ipod touch", new NoTouchTextProcessor());

		VectorSearch  vectorSearch = new VectorSearch(parser.getQuery(), new MemoryIndexReader());
		List<VectorRankedResult> result = vectorSearch.search();

		Assert.assertEquals( 1, result.size() );
		for (VectorRankedResult vrr : result) {
			Assert.assertEquals( 0, vrr.getKey().getId() );
		}
	}

	@Test
	public void AndTermBehavesLikeAnotherTerm() {
		SingleAttributeEntity ipod    = new SingleAttributeEntity(0,"apple ipod");
		SingleAttributeEntity iphone  = new SingleAttributeEntity(1,"apple iphone");
		SingleAttributeEntity zune  = new SingleAttributeEntity(2,"microsoft zune");
		SingleAttributeEntity and  = new SingleAttributeEntity(3,"+and");

		Utils.setupSampleMemoryIndex(ipod, iphone, zune, and);

		VectorQuery query = new VectorQueryParser("zune +AND apple").getQuery();

		List<VectorRankedResult> result = new VectorSearch(query, new MemoryIndexReader()).search();

		Assert.assertEquals(4, result.size() );

		for (VectorRankedResult vectorResult : result) {
			if((Integer)vectorResult.getKey().getId() == 0) {
				Assert.assertEquals(SingleAttributeEntity.class , vectorResult.getKey().getClazz() );				
			} else if((Integer)vectorResult.getKey().getId() == 1) {
				Assert.assertEquals(SingleAttributeEntity.class , vectorResult.getKey().getClazz() );				
			} else if((Integer)vectorResult.getKey().getId() == 2) {
				Assert.assertEquals(SingleAttributeEntity.class , vectorResult.getKey().getClazz() );
			} else if((Integer)vectorResult.getKey().getId() == 3) {
				Assert.assertEquals(SingleAttributeEntity.class , vectorResult.getKey().getClazz() );
			} else
				Assert.fail();
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
		
		List<VectorRankedResult> result = new VectorSearch(new VectorQueryParser("both").getQuery(), new MemoryIndexReader()).search();
		Assert.assertEquals(4, result.size() );			

		result = new VectorSearch(new VectorQueryParser("container").getQuery(), new MemoryIndexReader()).search();
		Assert.assertEquals(1, result.size() );			
		
		result = new VectorSearch(new VectorQueryParser("self").getQuery(), new MemoryIndexReader()).search();
		Assert.assertEquals(3, result.size() );			
	}
}
