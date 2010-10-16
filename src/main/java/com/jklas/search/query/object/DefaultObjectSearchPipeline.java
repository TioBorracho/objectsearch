package com.jklas.search.query.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.index.IndexId;
import com.jklas.search.index.ObjectKey;
import com.jklas.search.index.PostingMetadata;
import com.jklas.search.index.Term;
import com.jklas.search.index.dto.IndexObjectDto;
import com.jklas.search.indexer.pipeline.DefaultIndexingPipeline;
import com.jklas.search.indexer.pipeline.SemiIndex;

public class DefaultObjectSearchPipeline implements ObjectSearchPipeline {

	private static final DefaultIndexingPipeline defaultIndexingPipeline = new DefaultIndexingPipeline(); 
	
	@Override
	public List<ObjectSearchElement> processObject(Object entity) throws IndexObjectException {
	
		SemiIndex semiIndex = defaultIndexingPipeline.processObject(entity);
		
		if(semiIndex.getSemiIndexMap().size() == 0) return Collections.emptyList();
		
		List<ObjectSearchElement> searchElements = new ArrayList<ObjectSearchElement>();
				
		
		for (Entry<IndexObjectDto, Map<Term,PostingMetadata>> semiIndexEntry: semiIndex.getSemiIndexMap().entrySet()) {
			IndexObjectDto current = semiIndexEntry.getKey();

			IndexId currentIndexId = current.getIndexId();

			Map<Term,PostingMetadata> termPostingMap = semiIndexEntry.getValue();
			Class<?> currentObjectClass = current.getEntity().getClass();
			Serializable currentObjectId = current.getId();

			for (Map.Entry<Term, PostingMetadata> entry: termPostingMap.entrySet()) {
				Term term = entry.getKey();
				ObjectKey key = new ObjectKey(currentObjectClass,currentObjectId);

				searchElements.add( new ObjectSearchElement(currentIndexId, term, key) );				
			}
		}

		
		return searchElements;
	}

}
