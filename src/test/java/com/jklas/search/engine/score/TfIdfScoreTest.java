package com.jklas.search.engine.score;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.jklas.search.engine.VectorSearch;
import com.jklas.search.engine.dto.VectorRankedResult;
import com.jklas.search.index.MasterAndInvertedIndexReader;
import com.jklas.search.index.Term;
import com.jklas.search.index.memory.MemoryIndexReader;
import com.jklas.search.query.vectorial.VectorQuery;
import com.jklas.search.query.vectorial.VectorQueryParser;
import com.jklas.search.util.SearchLibrary;
import com.jklas.search.util.Utils;
import com.jklas.search.util.Utils.SingleAttributeEntity;

public class TfIdfScoreTest {

	@Test
	public void TfIdfInformationIsCorrectlyRetrievedFromIndex() {

		//		TF	DF
		// entity	1,1,1	3	
		// first	1,0,0	1
		// second	0,2,0	1
		// third	0,0,3	1

		SingleAttributeEntity firstEntity  = new SingleAttributeEntity(0,"first entity");

		SingleAttributeEntity secondEntity = new SingleAttributeEntity(1,"second second entity");

		SingleAttributeEntity thirdEntity  = new SingleAttributeEntity(2,"third third third entity");


		Utils.setupSampleMemoryIndex(firstEntity, secondEntity, thirdEntity);

		MasterAndInvertedIndexReader reader = new MemoryIndexReader();
		
		reader.open();

		Assert.assertEquals(3, reader.getIndexedObjectCount());

		Assert.assertEquals(3,	reader.read(new Term("ENTITY")).getTermCount());

		Assert.assertEquals(1,	reader.read(new Term("FIRST")).getTermCount());

		Assert.assertEquals(2,	reader.read(new Term("SECOND")).getTermCount());

		Assert.assertEquals(3,	reader.read(new Term("THIRD")).getTermCount());
		
		reader.close();
	}

	@Test
	public void TfIsCorrectlyExtractedFromObject() {
		/*
					TF-DOC1	TF-DOC2	TF-DOC3	DF	IDF (log N/DF)
			pedro	1		0		1		2	0,176091259
			de		1		0		0		1	0,477121255
			mendoza	1		0		0		1	0,477121255
			julian	0		1		0		1	0,477121255
			klas	0		1		1		2	0,176091259
			SCORE	1,130	0,6532	0,3522		
			N=	3				 		
		 */
		
		SingleAttributeEntity firstEntity  = new SingleAttributeEntity(0,"Pedro De Mendoza");

		SingleAttributeEntity secondEntity = new SingleAttributeEntity(1,"Julian Klas");

		SingleAttributeEntity thirdEntity  = new SingleAttributeEntity(2,"Pedro Klas");

		Utils.setupSampleMemoryIndex(firstEntity, secondEntity, thirdEntity);

		VectorQuery vectorQuery = new VectorQueryParser("Pedro Klas").getQuery();
		VectorSearch vectorSearch = new VectorSearch(vectorQuery, new MemoryIndexReader());

		List<VectorRankedResult> result = vectorSearch.search(new DefaultVectorRanker());

		// orden
		Assert.assertEquals(2,result.get(0).getKey().getId());

		// scores
		Assert.assertTrue(0.3521d == SearchLibrary.trunc(result.get(0).getScore(), 4));
		Assert.assertTrue(0.1760d == SearchLibrary.trunc(result.get(1).getScore(), 4));
		Assert.assertTrue(0.1760d == SearchLibrary.trunc(result.get(2).getScore(), 4));
	}


	@Test
	public void BasicTfIdfCalc(){
		//			TF		DF	IDF[log N / df]
		// entity	1,1,1	3	0	
		// first	1,0,0	1	0,477121255
		// second	0,2,0	1	0,477121255
		// third	0,0,3	1	0,477121255

		// sum(tf*idf) = 0,477121255
		SingleAttributeEntity firstEntity  = new SingleAttributeEntity(0,"first entity");
		// sum(tf*idf) = 0,954242509
		SingleAttributeEntity secondEntity = new SingleAttributeEntity(1,"second second entity");
		// sum(tf*idf) = 1,431363764
		SingleAttributeEntity thirdEntity  = new SingleAttributeEntity(2,"third third third entity");

		Utils.setupSampleMemoryIndex(firstEntity, secondEntity, thirdEntity);

		VectorQuery vectorQuery = new VectorQueryParser("first second third entity").getQuery();
		VectorSearch vectorSearch = new VectorSearch(vectorQuery, new MemoryIndexReader());

		List<VectorRankedResult> result = vectorSearch.search(new DefaultVectorRanker());

		for (VectorRankedResult vectorResult : result) {
			if((Integer)vectorResult.getKey().getId() == 0)
				Assert.assertEquals(SearchLibrary.trunc(0.4771,4), SearchLibrary.trunc(vectorResult.getScore(),4));
			else if((Integer)vectorResult.getKey().getId() == 1)
				Assert.assertEquals(SearchLibrary.trunc(0.9542d,4), SearchLibrary.trunc(vectorResult.getScore(),4));
			else if((Integer)vectorResult.getKey().getId() == 2)
				Assert.assertEquals(SearchLibrary.trunc(1.4313d,4), SearchLibrary.trunc(vectorResult.getScore(),4));
			else 
				Assert.fail();
		}

	}	

}
