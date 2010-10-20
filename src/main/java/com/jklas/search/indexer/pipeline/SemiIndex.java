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
package com.jklas.search.indexer.pipeline;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jklas.search.index.PostingMetadata;
import com.jklas.search.index.Term;
import com.jklas.search.index.dto.IndexObjectDto;

public class SemiIndex {

	private Map<IndexObjectDto,Map<Term, PostingMetadata>> semiIndexMap;
	
	public SemiIndex() {
		semiIndexMap = new HashMap<IndexObjectDto,Map<Term, PostingMetadata>>();
	}
	
	public SemiIndex(IndexObjectDto indexObjectDto, Map<Term, PostingMetadata> map) {
		this();
		semiIndexMap.put(indexObjectDto,map);
	}

	public SemiIndex(SemiIndex other) {
		this();
				
		for (Entry<IndexObjectDto,Map<Term, PostingMetadata>> entry : other.getSemiIndexMap().entrySet()) {
			semiIndexMap.put(entry.getKey(), entry.getValue());
		}
	}

	public Map<IndexObjectDto, Map<Term, PostingMetadata>> getSemiIndexMap() {
		return semiIndexMap;
	}

	public void setReferences(Object entity) {
		Map<IndexObjectDto,Map<Term, PostingMetadata>> newSemiIndexMap = new HashMap<IndexObjectDto,Map<Term, PostingMetadata>>();  

		IndexObjectDto key = new IndexObjectDto(entity);
		
		for (Map<Term, PostingMetadata> value : semiIndexMap.values()) {
			newSemiIndexMap.put(key, new HashMap<Term, PostingMetadata>(value));			
		}
		
		this.semiIndexMap = newSemiIndexMap;
	}
	
	public void merge(SemiIndex otherSemiIndex) {		
		Map<IndexObjectDto, Map<Term, PostingMetadata>> semiIndexMapToMerge = otherSemiIndex.getSemiIndexMap();
		
		for (IndexObjectDto objectToMerge : semiIndexMapToMerge.keySet())
		{
			Map<Term, PostingMetadata> termMetadataMapToMerge = semiIndexMapToMerge.get(objectToMerge);

			if(!semiIndexMap.containsKey(objectToMerge)) {
				semiIndexMap.put(objectToMerge,termMetadataMapToMerge);
			} else {				
				Map<Term, PostingMetadata> currentTermMetadataMap = semiIndexMap.get(objectToMerge);
								
				for (Term currentTermToMerge : termMetadataMapToMerge.keySet()) {
					PostingMetadata currentMetadataToMerge = termMetadataMapToMerge.get(currentTermToMerge);
					if(currentTermMetadataMap.containsKey(currentTermToMerge)) {
						currentTermMetadataMap.get(currentTermToMerge).merge(currentMetadataToMerge);
					} else {
						currentTermMetadataMap.put(currentTermToMerge,currentMetadataToMerge);
					}
				}	
			}
		}
	}
	
	public void buildMetadataFromTerms(Object entity, Field currentField, List<Term> fieldPostings)  {
		Map<Term, Integer> termCount = new HashMap<Term, Integer>(); 

		// Calculate term frequency for each term based on current field
		for (Term token : fieldPostings) {
			Integer count = termCount.get(token);

			if(count==null) count=1;
			else count++;

			termCount.put(token, count);
		}

		Map<Term, PostingMetadata> entityTermMetadataMap = getSemiIndexMap().get(entity);
		
		// Merge the (term;tf) pairs adding this field to the metadata
		for (Map.Entry<Term, Integer> count : termCount.entrySet()) {

			Term currentTerm = count.getKey();
			PostingMetadata postingMetadata = entityTermMetadataMap.get(currentTerm);

			if(postingMetadata == null) {
				postingMetadata = new PostingMetadata();				
			} 

			postingMetadata.addOrPutTf(currentField, count.getValue());

			postingMetadata.addSourceField(currentField);

			entityTermMetadataMap.put( currentTerm , postingMetadata );	
		}
	}	
}
