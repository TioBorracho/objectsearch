package com.jklas.search.indexer.pipeline;

import com.jklas.search.exception.IndexObjectException;

public interface IndexingPipeline {
	
	public SemiIndex processObject(Object entity) throws IndexObjectException;
}
