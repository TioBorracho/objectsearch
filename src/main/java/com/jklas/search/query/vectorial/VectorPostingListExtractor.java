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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jklas.search.engine.dto.SingleTermObjectResult;
import com.jklas.search.index.ObjectKey;
import com.jklas.search.index.PostingList;
import com.jklas.search.index.PostingMetadata;
import com.jklas.search.index.Term;
import com.jklas.search.query.PostingListExtractor;

public class VectorPostingListExtractor extends PostingListExtractor<SingleTermObjectResult> {

	private HashMap<Term, Integer> documentFrequency = new HashMap<Term, Integer>();
	
	@Override
	public SingleTermObjectResult createObjectResult(Term term, Entry<ObjectKey, PostingMetadata> entry) {
		
		Map<Field,Object> storedFields = null;
		
		PostingMetadata metadata = entry.getValue();
		
		for (Field potentiallyStoredField: metadata.getStoredFields()) {			
			if(metadata.isStoredField(potentiallyStoredField)) {
				if(storedFields == null) storedFields = new HashMap<Field,Object>();
				storedFields.put(potentiallyStoredField, metadata.getStoredFieldValue(potentiallyStoredField));
			}
		}
		
		return new SingleTermObjectResult(term, entry.getKey(),entry.getValue(), storedFields);
	}

	@Override
	protected void afterExtraction(Term term, PostingList postingList) {
		Integer currentDf = documentFrequency.get(term);
		
		if(currentDf == null) currentDf = 0;
		
		documentFrequency.put(term, currentDf + postingList.size());
	}
	
	public HashMap<Term, Integer> getDocumentFrequency() {
		return documentFrequency;
	}

}
