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
