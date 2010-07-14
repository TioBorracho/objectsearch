package com.jklas.search.query.object;

import java.util.ArrayList;
import java.util.List;

import com.jklas.search.SearchEngine;
import com.jklas.search.configuration.SearchConfiguration;
import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.exception.SearchEngineException;
import com.jklas.search.index.IndexId;
import com.jklas.search.index.Term;
import com.jklas.search.query.vectorial.VectorQuery;
import com.jklas.search.query.vectorial.VectorQueryParser;

public class VectorObjectQueryParser {

	private final ObjectSearchPipeline searchPipeline;
	
	private final Object queryObject;

	public VectorObjectQueryParser(Object queryObject) {
		SearchConfiguration configuration = SearchEngine.getInstance().getConfiguration();		
		if(!configuration.isMapped(queryObject)) throw new RuntimeException("Can't find mapping for class "+queryObject.getClass());
			
		this.queryObject = queryObject;
		this.searchPipeline = new DefaultObjectSearchPipeline();
	}
	
	public VectorQuery getQuery() throws SearchEngineException {
		
		SearchConfiguration configuration = SearchEngine.getInstance().getConfiguration();
		
		List<ObjectSearchElement> searchElements ;
		try {
			searchElements = searchPipeline.processObject(queryObject);			
		} catch (IndexObjectException e) {
			throw new SearchEngineException("Couldn't process the query object",e);
		}
		
		IndexId selectedIndex = configuration.getMapping(queryObject.getClass()).getIndexSelector().selectIndex(queryObject);
		
		List<Term> vectorTerms = new ArrayList<Term>(); 
		for (ObjectSearchElement searchElement : searchElements) {
			vectorTerms.add(searchElement.getTerm());
		}
		
		return new VectorQueryParser(vectorTerms).getQuery(selectedIndex);
	}

	
}
