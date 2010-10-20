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
