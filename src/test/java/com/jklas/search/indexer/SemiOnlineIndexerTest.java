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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jklas.search.SearchEngine;
import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.exception.SearchEngineMappingException;
import com.jklas.search.index.dto.IndexObject;
import com.jklas.search.index.memory.MemoryIndex;
import com.jklas.search.index.memory.MemoryIndexWriterFactory;
import com.jklas.search.indexer.pipeline.DefaultIndexingPipeline;
import com.jklas.search.indexer.semionline.SemiOnlineIndexer;
import com.jklas.search.indexer.semionline.SemiOnlineWorkerPool;
import com.jklas.search.indexer.semionline.SingleThreadWorkerPool;
import com.jklas.search.util.Utils;
import com.jklas.search.util.Utils.SingleAttributeEntity;

public class SemiOnlineIndexerTest {

	@Before
	public void startup() {
		SearchEngine.getInstance().reset();
		MemoryIndex.renewAllIndexes();
	}

	@Test
	public void SingleThreadPoolReceivesAnObjectWithoutExceptions() throws SearchEngineMappingException, IndexObjectException{
		DefaultIndexerService indexerService = new DefaultIndexerService(new DefaultIndexingPipeline(), MemoryIndexWriterFactory.getInstance());
		SemiOnlineWorkerPool pool = new SingleThreadWorkerPool(indexerService);
		SemiOnlineIndexer semiOnlineIndexer = new SemiOnlineIndexer( pool );

		SingleAttributeEntity entity = new Utils.SingleAttributeEntity(0,"Juli�n");
		Utils.configureAndMap(Utils.SingleAttributeEntity.class);

		pool.setGlobalTaskCount(1);
		pool.start();
		
		try {
			semiOnlineIndexer.create(new IndexObject(entity,0));		
		} finally {
			semiOnlineIndexer.destroy();
		}

		Assert.assertTrue(true);
	}

	@Test
	public void SingleThreadPoolIndexesObjects() throws SearchEngineMappingException, IndexObjectException, InterruptedException{
		DefaultIndexerService indexerService = new DefaultIndexerService(new DefaultIndexingPipeline(), MemoryIndexWriterFactory.getInstance());
		SemiOnlineWorkerPool pool = new SingleThreadWorkerPool(indexerService);
		SemiOnlineIndexer semiOnlineIndexer = new SemiOnlineIndexer( pool );

		semiOnlineIndexer.enableWait();

		pool.setGlobalTaskCount(10);
		pool.start();
		
		for (int i = 0; i < 10; i++) {
			SingleAttributeEntity entity = new Utils.SingleAttributeEntity(i,"Juli�n");
			if(i==0) Utils.configureAndMap(entity);			
			semiOnlineIndexer.create(entity);		
		}
		semiOnlineIndexer.waitForUnfinishedWorks();

		Assert.assertEquals(10,MemoryIndex.getDefaultIndex().getObjectCount());
	}

	@Test
	public void MultiThreadPoolReceivesAnObjectWithoutExceptions() throws SearchEngineMappingException, IndexObjectException{
		DefaultIndexerService indexerService = new DefaultIndexerService(new DefaultIndexingPipeline(), MemoryIndexWriterFactory.getInstance());
		SemiOnlineWorkerPool pool = new FixedThreadWorkerPool(indexerService);
		SemiOnlineIndexer semiOnlineIndexer = new SemiOnlineIndexer( pool );

		SingleAttributeEntity entity = new Utils.SingleAttributeEntity(0,"Juli�n");
		Utils.configureAndMap(Utils.SingleAttributeEntity.class);
		
		pool.start();
		
		try {
			semiOnlineIndexer.create(new IndexObject(entity,0));		
		} finally {
			semiOnlineIndexer.destroy();
		}

		Assert.assertTrue(true);
	}


	@Test
	public void MultiThreadPoolIndexesObjects() throws SearchEngineMappingException, IndexObjectException, InterruptedException{

		SearchEngine.getInstance().newConfiguration();
		Utils.configureAndMap(Utils.SingleAttributeEntity.class);
		
		DefaultIndexerService indexerService = new DefaultIndexerService(new DefaultIndexingPipeline(), MemoryIndexWriterFactory.getInstance());
		SemiOnlineWorkerPool pool = new FixedThreadWorkerPool(indexerService,20);

		SemiOnlineIndexer semiOnlineIndexer = new SemiOnlineIndexer( pool );
		semiOnlineIndexer.enableWait();
		int numberOfObjectsToIndex = 1000;		
		pool.setGlobalTaskCount(numberOfObjectsToIndex );
		pool.start();

		for (int i = 0; i < numberOfObjectsToIndex; i++) {
			SingleAttributeEntity entity = new Utils.SingleAttributeEntity(i,"Juli�n");
			if(i==0) Utils.configureAndMap(entity);			
			semiOnlineIndexer.create(entity);		
		}
		
		semiOnlineIndexer.waitForUnfinishedWorks();

		Assert.assertEquals(numberOfObjectsToIndex, MemoryIndex.getDefaultIndex().getObjectCount() );
	}

}
