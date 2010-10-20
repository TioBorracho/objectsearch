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
package com.jklas.search.index;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class PostingList implements Iterable<Entry<ObjectKey,PostingMetadata>>, Serializable {

	private static final long serialVersionUID = -9201035548715576822L;

	private final Term term;
	
	private Map<ObjectKey,PostingMetadata> postings ;

	public PostingList(Term term) {
		this.term = term;
		this.postings = new ConcurrentHashMap<ObjectKey,PostingMetadata>();
	}

	public void add(ObjectKey key, PostingMetadata newMetadata) {
		postings.put(key, newMetadata);			
	}

	public void remove(ObjectKey key) {		
		PostingMetadata oldMetadata = postings.get(key);

		if(oldMetadata !=null) {
			postings.remove(key);
		}
	}

	public boolean contains(ObjectKey key) {
		return postings.containsKey(key);
	}

	@Override
	public Iterator<Entry<ObjectKey,PostingMetadata>> iterator() {		
		return new TfAwareIterator(this,postings.entrySet().iterator());
	}

	public Integer size() {
		return postings.size();
	}

	public boolean isSorted() {
		return false;
	}

	private class TfAwareIterator implements Iterator<Map.Entry<ObjectKey, PostingMetadata>> {

		private Iterator<Map.Entry<ObjectKey, PostingMetadata>> entryIterator;

		private Map.Entry<ObjectKey, PostingMetadata> currentEntry = null;

		private PostingList postingList;

		public TfAwareIterator(PostingList postingList, Iterator<Map.Entry<ObjectKey, PostingMetadata>> entryIterator) {
			this.entryIterator = entryIterator;
			this.postingList = postingList;
		}

		@Override
		public boolean hasNext() {			
			return entryIterator.hasNext();
		}

		@Override
		public Entry<ObjectKey, PostingMetadata> next() {
			currentEntry = entryIterator.next();
			return currentEntry;
		}

		@Override
		public void remove() {
			postingList.remove(currentEntry.getKey());
		}		
	}

	public int getTermCount() {
		int globalCount = 0;
		for (Iterator<Entry<ObjectKey, PostingMetadata>> iterator = iterator(); iterator.hasNext();) {
			PostingMetadata currentMetadata = iterator.next().getValue();

			for (Integer currentFieldTf : currentMetadata.getFieldTfMap().values()) {
				globalCount += currentFieldTf;
			}
		}
		return globalCount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		for(Entry<ObjectKey,PostingMetadata> posting : postings.entrySet()) {
			builder.append(posting.getKey());
			builder.append("\n");
			builder.append(posting.getValue());
		}

		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PostingList other = (PostingList) obj;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		return true;
	}

	
	
}
