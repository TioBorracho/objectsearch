package com.jklas.search.query.object;

import java.util.List;

import com.jklas.search.exception.IndexObjectException;

public interface ObjectSearchPipeline {

	public List<ObjectSearchElement> processObject(Object entity) throws IndexObjectException;
	
}
