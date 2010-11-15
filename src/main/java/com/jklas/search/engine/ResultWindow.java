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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.jklas.search.engine.dto.ObjectKeyResult;
import com.jklas.search.engine.dto.ObjectResult;
import com.jklas.search.engine.dto.VectorRankedResult;
import com.jklas.search.query.SearchQuery;

/**
 * This utility class takes care of result windowing (that is, cutting the result
 * lists to fit the desired size).
 * 
 * @author Julián Klas
 *
 */
public class ResultWindow {

	public static Set<VectorRankedResult> windowVectorSet(Set<? extends ObjectResult> retrieved, SearchQuery query) {
		Set<VectorRankedResult> result = new HashSet<VectorRankedResult>(query.getPageSize());
		vectorWindowing(retrieved, result, query.getPage(), query.getPageSize());
		return result;
	}

	public static List<VectorRankedResult> windowVectorList(List<? extends ObjectResult> retrieved, SearchQuery query) {
		List<VectorRankedResult> result = new ArrayList<VectorRankedResult>(query.getPageSize());
		vectorWindowing(retrieved, result, query.getPage(), query.getPageSize());			
		return result;
	}
	
	public static Set<ObjectKeyResult> windowSet(Set<? extends ObjectResult> retrieved, SearchQuery query) {
		Set<ObjectKeyResult> result = new HashSet<ObjectKeyResult>(query.getPageSize());
		booleanWindowing(retrieved, result, query.getPage(), query.getPageSize());
		return result;
	}

	public static List<ObjectKeyResult> windowList(List<? extends ObjectResult> retrieved, SearchQuery query) {
		List<ObjectKeyResult> result = new ArrayList<ObjectKeyResult>(query.getPageSize());
		booleanWindowing(retrieved, result, query.getPage(), query.getPageSize());			
		return result;
	}

	private static void vectorWindowing(Collection<? extends ObjectResult> retrieved, Collection<VectorRankedResult> result, int page, int pageSize) {
		int currentIndex = 0;		
		int startIndex = (page-1) * pageSize + 1;
		int endIndex = page * pageSize;
		
		for (Iterator<? extends ObjectResult> iterator = retrieved.iterator(); iterator.hasNext() && currentIndex < endIndex ;) {
			VectorRankedResult objectKeyResult = (VectorRankedResult) iterator.next();

			currentIndex++;
			
			if(currentIndex < startIndex) continue;
			
			result.add(objectKeyResult);
			
		}
	}
	
	private static void booleanWindowing(Collection<? extends ObjectResult> retrieved, Collection<ObjectKeyResult> result, int page, int pageSize) {
		int currentIndex = 0;		
		int startIndex = (page-1) * pageSize + 1;
		int endIndex = page * pageSize;
		
		for (Iterator<? extends ObjectResult> iterator = retrieved.iterator(); iterator.hasNext() && currentIndex < endIndex ;) {
			ObjectKeyResult objectKeyResult = (ObjectKeyResult) iterator.next();

			currentIndex++;
			
			if(currentIndex < startIndex) continue;
			
			result.add(objectKeyResult);
			
		}
	}

}
