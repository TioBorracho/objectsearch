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
package com.jklas.search.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jklas.search.engine.dto.ObjectResult;
import com.jklas.search.engine.dto.SingleTermObjectResult;
import com.jklas.search.engine.dto.VectorRankedResult;
import com.jklas.search.engine.filter.FilterChain;
import com.jklas.search.engine.score.VectorRanker;
import com.jklas.search.index.IndexReaderFactory;
import com.jklas.search.index.MasterAndInvertedIndexReader;
import com.jklas.search.index.ObjectKey;
import com.jklas.search.query.operator.Operator;
import com.jklas.search.query.vectorial.VectorQuery;
import com.jklas.search.sort.PreSort;

public class VectorSearch implements Search {

	private final VectorQuery query;

	private final MasterAndInvertedIndexReader reader;

	public VectorSearch(VectorQuery vectorQuery, IndexReaderFactory indexReaderFactory) {
		this( vectorQuery , indexReaderFactory.getIndexReader( vectorQuery.getSelectedIndex() ));
	}

	public VectorSearch(VectorQuery vectorQuery, MasterAndInvertedIndexReader reader) {
		checkParameters(vectorQuery,reader);
		this.reader = reader;
		this.query = vectorQuery;		
	}

	public List<VectorRankedResult> search(VectorRanker ranker, Comparator<? super VectorRankedResult> comparator ) {
		try {						
			List<VectorRankedResult> rankedResults = ranker.rank(query, retrieve(), reader);

			Collections.sort(rankedResults, comparator);

			return ResultWindow.windowVectorList( rankedResults, query );
		} finally {
			if(reader.isOpen()) reader.close();
		}
	}

	/* (non-Javadoc)
	 * @see com.jklas.search.engine.Search#search()
	 */
	@Override
	public List<VectorRankedResult> search() {
		try {
			return ResultWindow.windowVectorList( retrieveAndCompactToVectorResult() , query);			
		} finally {
			if(reader.isOpen()) reader.close();
		}
	}

	public List<VectorRankedResult> search(VectorRanker ranker) {
		try {
			return ResultWindow.windowVectorList( ranker.rank(query, retrieve(), reader) , query);					
		} finally {
			if(reader.isOpen()) reader.close();
		}
	}	

	/* (non-Javadoc)
	 * @see com.jklas.search.engine.Search#search(com.jklas.search.engine.filter.FilterChain)
	 */
	@Override
	public List<VectorRankedResult> search(FilterChain filterChain) {
		try {
			List<VectorRankedResult> results = search();		
			filterChain.applyFilters(results);
			ResultWindow.windowVectorList( results, query);
			return results;					
		} finally {
			if(reader.isOpen()) reader.close();
		}
	}

	/* (non-Javadoc)
	 * @see com.jklas.search.engine.Search#search(java.util.Comparator)
	 */
	@Override
	public List<VectorRankedResult> search(Comparator<? super ObjectResult> comparator) {
		try {
			ArrayList<VectorRankedResult> results = retrieveAndCompactToVectorResult();		
			Collections.sort(results, comparator);
			ResultWindow.windowVectorList( results, query );
			return results;					
		} finally {
			if(reader.isOpen()) reader.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.jklas.search.engine.Search#search(com.jklas.search.sort.PreSort)
	 */
	@Override
	public List<VectorRankedResult> search(PreSort rule) {
		try {			
			return ResultWindow.windowVectorList( rule.work(search()) , query);					
		} finally {
			if(reader.isOpen()) reader.close();
		}
	}

	/**
	 * Compacts the results from the inverted index into a list
	 * of VectorRankedResult, discarding the TF IDF information stored
	 * on the index
	 * 
	 * @return
	 */
	private ArrayList<VectorRankedResult> retrieveAndCompactToVectorResult() {
		Set<SingleTermObjectResult> unrankedRetrievedSet = retrieve();

		ArrayList<VectorRankedResult> results = new ArrayList<VectorRankedResult>(unrankedRetrievedSet.size());

		Set<ObjectKey> alreadyAddedToList = new HashSet<ObjectKey>();

		for (SingleTermObjectResult singleTermObjectResult : unrankedRetrievedSet) {
			ObjectKey key = singleTermObjectResult.getKey();

			if(alreadyAddedToList.contains(key)) continue;
			else {
				alreadyAddedToList.add(key);
				results.add(new VectorRankedResult(key, singleTermObjectResult.getStoredFields()));				
			}
		}
		return results;
	}

	private Set<SingleTermObjectResult> retrieve() {
		Operator<SingleTermObjectResult> rootOperator = query.getRootOperator();
		
		reader.open( query.getSelectedIndex() );

		// TODO A esta altura todavia no hay que eliminar los duplicados porque
		// en la búsqueda rankeada quiero poder ver todos los postings (para calcular 
		// correctamente el TF. Hay que cambiar el VectorResult por un IndexObjectResult
		// y luego en la etapa de scoring pasar a VectorResult			
		Set<SingleTermObjectResult> vectorQueryResults = rootOperator.work(reader);

		return vectorQueryResults;
	}

	private void checkParameters(VectorQuery query, MasterAndInvertedIndexReader reader) {
		if(query == null)
			throw new IllegalArgumentException("Can't search for a null query");

		if(reader == null)
			throw new IllegalArgumentException("Can't search using a null reader");
	}

}
