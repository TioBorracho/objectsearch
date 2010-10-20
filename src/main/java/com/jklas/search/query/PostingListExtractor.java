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
