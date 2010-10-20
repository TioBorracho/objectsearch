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
package com.jklas.search.engine.score; 

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jklas.search.engine.dto.SingleTermObjectResult;
import com.jklas.search.engine.dto.VectorRankedResult;
import com.jklas.search.index.MasterAndInvertedIndexReader;
import com.jklas.search.index.ObjectKey;
import com.jklas.search.index.Term;
import com.jklas.search.query.vectorial.VectorQuery;

public class DefaultVectorRanker implements VectorRanker {


	/* (non-Javadoc)
	 * @see com.jklas.search.engine.score.VectorRanker#rank(com.jklas.search.query.vectorial.VectorQuery, java.util.Set, com.jklas.search.index.MasterAndInvertedIndexReader)
	 */
	@Override
	public List<VectorRankedResult> rank(VectorQuery vectorQuery, Set<SingleTermObjectResult> unsortedResults, MasterAndInvertedIndexReader reader) {

		List<VectorRankedResult> unsortedRankedResults = applyScores(vectorQuery, unsortedResults, reader);

		return sort(unsortedRankedResults);
	}

	private List<VectorRankedResult> applyScores(VectorQuery vectorQuery, Set<SingleTermObjectResult> unsortedResults, MasterAndInvertedIndexReader reader) {
		HashMap<Term,Integer> df = new HashMap<Term,Integer>(vectorQuery.getExtractor().getDocumentFrequency());

		Map<ObjectKey,VectorRankedResult> rankedResultMap = new HashMap<ObjectKey,VectorRankedResult>();
		
		int indexedObjectCount = reader.getIndexedObjectCount();

		for (SingleTermObjectResult singleTermMatch : unsortedResults) {

			ObjectKey key = singleTermMatch.getKey();

			VectorRankedResult objectResult = rankedResultMap.get(key);

			if(objectResult == null) {
				objectResult = new VectorRankedResult(key, 0);
				rankedResultMap.put(key,objectResult);
			} 			

			objectResult.addScore(
					getScore( singleTermMatch , df.get( singleTermMatch.getTerm() ) , indexedObjectCount )
			);
		}

		return new ArrayList<VectorRankedResult>(rankedResultMap.values());			
	}

	private double getScore(SingleTermObjectResult uor, Integer termDf, double totalObjects) {
		int allFieldsTf = 0;

		for(Integer currentFieldTf: uor.getMetadata().getFieldTfMap().values()) {
			allFieldsTf += currentFieldTf;
		}

		double idf = Math.log10( totalObjects / termDf );

		return ((double)allFieldsTf) * idf; 
	}

	private List<VectorRankedResult> sort(List<VectorRankedResult> unsortedRankedResults) {
		Collections.sort(unsortedRankedResults);
		return unsortedRankedResults;
	}


}
