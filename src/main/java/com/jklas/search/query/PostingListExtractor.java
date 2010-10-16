package com.jklas.search.query;

import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import com.jklas.search.engine.dto.ObjectResult;
import com.jklas.search.index.ObjectKey;
import com.jklas.search.index.PostingList;
import com.jklas.search.index.PostingMetadata;
import com.jklas.search.index.Term;

public abstract class PostingListExtractor<E extends ObjectResult> {

	protected abstract E createObjectResult(Term term, Entry<ObjectKey, PostingMetadata> entry);
	
	public Set<E> extract(Term term, PostingList postingList) {
		
		Set<E> retrieved = new HashSet<E>();
		
		if(postingList!=null) {
			for (Entry<ObjectKey, PostingMetadata> entry : postingList) {
				retrieved.add(createObjectResult(term, entry));
			}
						
			afterExtraction(term, postingList);
		}
		
		return retrieved;		
	}

	protected void afterExtraction(Term term, PostingList postingList) {};
	
}
