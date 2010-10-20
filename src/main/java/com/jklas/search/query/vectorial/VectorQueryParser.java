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
package com.jklas.search.query.vectorial;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jklas.search.engine.Language;
import com.jklas.search.engine.dto.SingleTermObjectResult;
import com.jklas.search.engine.processor.DefaultQueryTextProcessor;
import com.jklas.search.engine.processor.QueryTextProcessor;
import com.jklas.search.index.IndexId;
import com.jklas.search.index.Term;
import com.jklas.search.query.operator.Operator;
import com.jklas.search.query.operator.OrOperator;
import com.jklas.search.query.operator.RetrieveOperator;

public class VectorQueryParser {

	private final List<Term> queryTokens;
	
	public VectorQueryParser(String originalQuery) {
		this(originalQuery, Language.UNKOWN_LANGUAGE); 
	}
	
	public VectorQueryParser(String originalQuery, Language language) {
		this(originalQuery, language, new DefaultQueryTextProcessor() ); 
	}

	public VectorQueryParser(String originalQuery, QueryTextProcessor queryTextProcessor) {
		this(originalQuery, Language.UNKOWN_LANGUAGE, queryTextProcessor);
	}
	
	public VectorQueryParser(String originalQuery, Language language, QueryTextProcessor queryTextProcessor) {		
		this(queryTextProcessor.processText(originalQuery, language));
	}
	
	public VectorQueryParser(List<Term> queryTerms) {		
		if(queryTerms == null) throw new IllegalArgumentException("Can't parse a null term list");
		this.queryTokens = queryTerms;
	}

	public VectorQuery getQuery(IndexId selectedIndex) {
		int tokenCount = queryTokens.size();

		if(tokenCount == 0)
			throw new IllegalArgumentException("Query must contain at least one term");
		
		Map<Term, Integer> termVectorMap = buildTermVectors(queryTokens);
		
		HashSet<Term> termSet = new HashSet<Term>();

		for (Iterator<Term> iterator = queryTokens.iterator(); iterator.hasNext();) {
			Term term = (Term) iterator.next();
			
			if(termSet.contains(term)) iterator.remove();
			else termSet.add(term);
		}
		
		VectorPostingListExtractor extractor = new VectorPostingListExtractor();
		
		Operator<SingleTermObjectResult> rootOperator = buildOrOperatorFromLeft(queryTokens, extractor, 0);
		
		return new VectorQuery( selectedIndex, termVectorMap, rootOperator, extractor);	
	}
	
	public VectorQuery getQuery() {
		return getQuery( IndexId.getDefaultIndexId() );
	}

	private Map<Term,Integer> buildTermVectors(List<Term> queryTokens) {
		HashMap<Term,Integer> termCount = new HashMap<Term,Integer>();
				
		for (Term term : queryTokens) {
			Integer count = termCount.get(term);			
			if(count == null) count = 0;			
			termCount.put(term, count+1);			
		}
	
		return termCount;
	}

	private Operator<SingleTermObjectResult> buildOrOperatorFromLeft(List<Term> queryTokens, VectorPostingListExtractor extractor, int i) {
		Term currentToken = queryTokens.get(i);
		
		if(queryTokens.size() > i+1) {
			return new OrOperator<SingleTermObjectResult>( new RetrieveOperator<SingleTermObjectResult>(currentToken,extractor),
					buildOrOperatorFromLeft(queryTokens, extractor, i+1));
		} else {
			return new RetrieveOperator<SingleTermObjectResult>(currentToken,extractor);			
		}
	}

}
