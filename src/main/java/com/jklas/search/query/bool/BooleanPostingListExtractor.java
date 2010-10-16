package com.jklas.search.query.bool;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jklas.search.engine.dto.ObjectKeyResult;
import com.jklas.search.index.ObjectKey;
import com.jklas.search.index.PostingMetadata;
import com.jklas.search.index.Term;
import com.jklas.search.query.PostingListExtractor;

public class BooleanPostingListExtractor extends PostingListExtractor<ObjectKeyResult> {

	@Override
	public ObjectKeyResult createObjectResult(Term term, Entry<ObjectKey, PostingMetadata> entry) {
		PostingMetadata metadata = entry.getValue();
		
		Map<Field,Object> storedFields = null;
		
		for (Field potentiallyStoredField: metadata.getStoredFields()) {			
			if(metadata.isStoredField(potentiallyStoredField)) {
				if(storedFields == null) storedFields = new HashMap<Field,Object>();
				storedFields.put(potentiallyStoredField, metadata.getStoredFieldValue(potentiallyStoredField));
			}
		}
		
		if(storedFields!=null)
			return new ObjectKeyResult(entry.getKey(), storedFields);
		else
			return new ObjectKeyResult(entry.getKey());
	}

}
