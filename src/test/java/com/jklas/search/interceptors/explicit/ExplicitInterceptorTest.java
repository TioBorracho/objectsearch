package com.jklas.search.interceptors.explicit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jklas.search.SearchEngine;
import com.jklas.search.index.memory.MemoryIndexWriterFactory;
import com.jklas.search.indexer.DefaultIndexerService;
import com.jklas.search.indexer.pipeline.DefaultIndexingPipeline;
import com.jklas.search.interceptors.SearchInterceptor;

public class ExplicitInterceptorTest {

	private class Dummy {}
	
	@Before
	public void setUp() {
		SearchEngine.getInstance().reset();
	}
	
	@Test
	public void unmappedObjectDoesntGetIndexed() {

		ExplicitInterceptor interceptor = new ExplicitInterceptor(
				new SearchInterceptor(
						new DefaultIndexerService(new DefaultIndexingPipeline(), MemoryIndexWriterFactory.getInstance())
					)
				);
		
		Dummy dummy = new Dummy();
				
		Assert.assertFalse(interceptor.create(dummy,new Integer(1)));
	}
	
	@Test
	public void unmappedObjectDoesntGetUnIndexed() {
		ExplicitInterceptor interceptor = new ExplicitInterceptor(
				new SearchInterceptor(
						new DefaultIndexerService(new DefaultIndexingPipeline(), MemoryIndexWriterFactory.getInstance())
					)
				);
		
		Dummy dummy = new Dummy();
		
		Assert.assertFalse(interceptor.delete(dummy,new Integer(1)));
	}
	
	@Test
	public void unmappedObjectDoesntGetUpdated() {
		ExplicitInterceptor interceptor = new ExplicitInterceptor(
				new SearchInterceptor(
						new DefaultIndexerService(new DefaultIndexingPipeline(), MemoryIndexWriterFactory.getInstance())
					)
				);
		
		Dummy dummy = new Dummy();
		
		Assert.assertFalse(interceptor.update(dummy,new Integer(1)));
	}
	
	
// TODO test que prueben que un objeto mapeado se indexa	
}
