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
