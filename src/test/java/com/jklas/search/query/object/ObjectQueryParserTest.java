package com.jklas.search.query.object;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.jklas.search.annotations.IndexSelector;
import com.jklas.search.annotations.Indexable;
import com.jklas.search.annotations.SearchField;
import com.jklas.search.annotations.SearchId;
import com.jklas.search.engine.VectorSearch;
import com.jklas.search.engine.dto.VectorRankedResult;
import com.jklas.search.exception.SearchEngineException;
import com.jklas.search.exception.SearchEngineMappingException;
import com.jklas.search.index.memory.MemoryIndexReaderFactory;
import com.jklas.search.util.Utils;


public class ObjectQueryParserTest {

	@Indexable(indexName="Books")
	@SuppressWarnings("unused")
	private class Tragedy {
		@SearchId public final int id;
		
		@SearchField public final String authorName;
		
		@SearchField public final String bookTitle;

		public Tragedy(int id, String author, String title) {
			this.id = id;
			this.authorName = author;
			this.bookTitle = title;
		}
	}

	@Indexable(indexName="Comedies")
	@SuppressWarnings("unused")
	private class Comedy {
		@SearchId public final int id;
		
		@SearchField public final String authorName;
		
		@SearchField public final String bookTitle;

		public Comedy(int id, String author, String title) {
			this.id = id;
			this.authorName = author;
			this.bookTitle = title;
		}		
		
	}

	@Indexable
	public class Author {
		@IndexSelector public String idx;
		@SearchId public final int id;
		@SearchField public final String authorName;
		public Author(int id, String authorName) {
			this.id = id; this.authorName = authorName;
		}
	}
	
	@Test
	public void ObjectAsQueryPlusIndexChange() throws SearchEngineException, SearchEngineMappingException {
		Utils.configureAndMap(Author.class);

		Tragedy othello = new Tragedy(0,"Shakespeare","Othello");
		Tragedy hamlet = new Tragedy(1,"Shakespeare","Hamlet");
		Comedy merchantOfVenice = new Comedy(0,"Shakespeare","The Merchant of Venice");
		Utils.setupSampleMemoryIndex(othello,hamlet,merchantOfVenice);
		
		Author shakespeare = new Author(0,"Shakespeare");
		shakespeare.idx = "Books";

		VectorObjectQueryParser parser = new VectorObjectQueryParser(shakespeare);		
		VectorSearch search = new VectorSearch(parser.getQuery(), MemoryIndexReaderFactory.getInstance() ); 
		List<VectorRankedResult> result = search.search();
		Assert.assertEquals(2, result.size() );
		
		shakespeare.idx = "Comedies";
		parser = new VectorObjectQueryParser(shakespeare);		
		search = new VectorSearch(parser.getQuery(), MemoryIndexReaderFactory.getInstance() ); 
		result = search.search();
		Assert.assertEquals(1, result.size() );

	}

	
}
