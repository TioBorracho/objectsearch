package com.jklas.search.indexer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jklas.search.SearchEngine;
import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.exception.SearchEngineMappingException;
import com.jklas.search.index.dto.IndexObjectDto;
import com.jklas.search.index.memory.MemoryIndex;
import com.jklas.search.index.memory.MemoryIndexWriterFactory;
import com.jklas.search.indexer.online.OnlineIndexer;
import com.jklas.search.indexer.pipeline.DefaultIndexingPipeline;
import com.jklas.search.util.Utils;
import com.jklas.search.util.Utils.SingleAttributeEntity;

public class OnlineIndexerTest {

    
    @Before
    public void startup() {
	SearchEngine.getInstance().reset();
	MemoryIndex.renewAllIndexes();
    }
    
    @Test
    public void SingleThreadPoolReceivesAnObjectWithoutExceptions() throws SearchEngineMappingException, IndexObjectException{
	DefaultIndexerService indexerService = new DefaultIndexerService(new DefaultIndexingPipeline(), MemoryIndexWriterFactory.getInstance());
	OnlineIndexer onlineIndexer = new OnlineIndexer( indexerService );

	SingleAttributeEntity entity = new Utils.SingleAttributeEntity(0,"Juli�n");
	Utils.configureAndMap(entity);

	onlineIndexer.create(new IndexObjectDto(entity,0));		
	
	Assert.assertTrue(true);
    }

    @Test
    public void SingleThreadPoolIndexesObjects() throws SearchEngineMappingException, IndexObjectException, InterruptedException{
	DefaultIndexerService indexerService = new DefaultIndexerService(new DefaultIndexingPipeline(), MemoryIndexWriterFactory.getInstance());
	OnlineIndexer onlineIndexer = new OnlineIndexer( indexerService );

	for (int i = 0; i < 10; i++) {
	    SingleAttributeEntity entity = new Utils.SingleAttributeEntity(i,"Juli�n");
	    if(i==0) Utils.configureAndMap(entity);			
	    onlineIndexer.create(entity);		
	    Assert.assertEquals(i+1,MemoryIndex.getDefaultIndex().getObjectCount());
	}
    }
}
