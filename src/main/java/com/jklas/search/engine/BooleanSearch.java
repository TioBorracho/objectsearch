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
import java.util.List;
import java.util.Set;

import com.jklas.search.engine.dto.ObjectKeyResult;
import com.jklas.search.engine.dto.ObjectResult;
import com.jklas.search.engine.filter.FilterChain;
import com.jklas.search.index.IndexReaderFactory;
import com.jklas.search.index.MasterAndInvertedIndexReader;
import com.jklas.search.query.bool.BooleanQuery;
import com.jklas.search.query.operator.Operator;
import com.jklas.search.sort.PreSort;

/**
 * This class orchestrates the boolean retrieval process.
 * 
 * Given a BooleanQuery and a MasterAndInvertedIndexReader,
 * objects of this class can retrieve information and 
 * colaborate with FilterChain and Comparators to
 * Filter and Sort the results.
 * 
 * @author Julián Klas (jklas@fi.uba.ar)
 * @date 03/2010
 */
public class BooleanSearch implements Search {

	/**
	 * This object has the responsability of holding
	 * all the information related to the user's information need. 
	 */
	private final BooleanQuery query;

	/**
	 * This reader has the responsability of low level 
	 * access to Master and Inverted indexes during
	 * boolean posting list retrieval.
	 */
	private final MasterAndInvertedIndexReader reader;

	/**
	 * Constructs a boolean query search object based on a BooleanQuery
	 * and a MasterAndInvertedIndexReader.
	 * 
	 * @param booleanQuery the query that will be executed
	 * @param reader an object that will read posting lists from memory, disk, database, etc
	 */
	public BooleanSearch(BooleanQuery booleanQuery, IndexReaderFactory factory) {
		this.reader = factory.getIndexReader();
		this.query = booleanQuery;
		checkParameters(booleanQuery,reader);
	}

	/**
	 * Executes the boolean query against the current index reader.
	 * 
	 * @return a set of object keys that can be used for
	 * filtering, sorting or hydration.
	 * 
	 */
	public Set<ObjectKeyResult> retrieve() {
		Operator<ObjectKeyResult> rootOperator = query.getRootOperator();
		try {
			reader.open( query.getSelectedIndex() );
			Set<ObjectKeyResult> booleanQueryResults = rootOperator.work(reader);			
			return booleanQueryResults;
		} finally {
			reader.close();			
		}
	}

	/**
	 * Executes the boolean query against the current index reader.
	 * 
	 * @return a set of object keys that can be used for
	 * filtering, sorting or hydration.
	 * 
	 */
	public Set<ObjectKeyResult> search() {
		Set<ObjectKeyResult> retrieved = retrieve();
		
		return ResultWindow.windowSet(retrieved, query);
	}

	
		
	/**
	 * Executes the boolean query against the current index reader.
	 * 
	 * This method uses the specified comparator to sort
	 * the query result set into an ordered list.
	 *
	 * @see BooleanSearch#search()
	 * 
	 * @param comparator the comparator for the objects on the result set
	 * @return an ordered list of results that matches the boolean query
	 */
	public List<ObjectKeyResult> search(Comparator<? super ObjectResult> comparator) {
		return ResultWindow.windowList( sortResults(search(), comparator) , query);
	}
	
	/**
	 * Executes the boolean query against the current index reader.
	 * 
	 * This method filters objects that do not comply with a filter
	 * criteria specified in the FilterChain's filters.
	 * 
	 * @see FilterChain
	 * @see BooleanSearch#search()
	 * 
	 * @param filterChain the chain that holds de ResultFilter's
	 * @return a set of object results that comply with FilterChain's ResultFilter criteria
	 */
	public Set<ObjectKeyResult> search(FilterChain filterChain) {		
		Set<ObjectKeyResult> results = search();		
		filterChain.applyFilters(results);		
		return ResultWindow.windowSet( results , query); 
	}


	/**
	 * Executes the boolean query against the current index reader.
	 * 
	 * This method is a combination of the {@link #search(FilterChain)}
	 * and the {@link #search(Comparator)} methods.
	 * 
	 * The first step is filtering and the second one is sorting (this
	 * heuristic takes into account that filtering is tipically a O(n)
	 * operation and sorting si O(n log n) so you'll tipically want
	 * to sort less than you filter.
	 * 
	 * @see FilterChain
	 * @see BooleanSearch#search()
	 * 
	 * @param filterChain the chain that holds de ResultFilter's
	 * @return a set of object results that comply with FilterChain's ResultFilter criteria
	 */
	public List<ObjectKeyResult> search(FilterChain filterChain, Comparator<? super ObjectKeyResult> comparator) {		
		return ResultWindow.windowList( sortResults(search(filterChain), comparator) , query); 
	}
	
	/**
	 * 
	 * Applies sorting based on the comparator.
	 * 
	 * Delegates sorting on Java standard libraries.
	 * 
	 * @see #search(Comparator)
	 * @see #search(FilterChain, Comparator)
	 * 
	 * @param booleanQueryResults the result set to be sorted.
	 * @param comparator the criteria to be used for comparison
	 * @return a sorted list of object result keys
	 */
	private List<ObjectKeyResult> sortResults(Set<ObjectKeyResult> booleanQueryResults, Comparator<? super ObjectKeyResult> comparator) {
		ArrayList<ObjectKeyResult> results = new ArrayList<ObjectKeyResult>( booleanQueryResults );
		Collections.sort(results, comparator);
		return results;
	}

	/**
	 * Checks that the query and the inverted index are legal parameters
	 */
	private void checkParameters(BooleanQuery query, MasterAndInvertedIndexReader reader) {
		if(query == null)
			throw new IllegalArgumentException("Can't search for a null query");

		if(reader == null)
			throw new IllegalArgumentException("Can't search using a null reader");
	}

	public List<? extends ObjectResult> search(PreSort rule) {
		return ResultWindow.windowList( rule.work(search()) , query);
	}

}

