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
