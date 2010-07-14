package com.jklas.search.index.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.jklas.search.index.IndexId;
import com.jklas.search.index.MasterAndInvertedIndex;
import com.jklas.search.index.MasterRegistryEntry;
import com.jklas.search.index.ObjectKey;
import com.jklas.search.index.PostingList;
import com.jklas.search.index.PostingMetadata;
import com.jklas.search.index.Term;

public final class MemoryIndex implements MasterAndInvertedIndex {

	private final static HashMap<IndexId,MemoryIndex> availableIndexes ;
	
	private final IndexId indexId;
	
	private Map<ObjectKey,MasterRegistryEntry> masterRegistry = new ConcurrentHashMap<ObjectKey,MasterRegistryEntry>();

	private Map<Term, PostingList> invertedIndex = new ConcurrentHashMap<Term,PostingList>();

	static {
		availableIndexes = new HashMap<IndexId,MemoryIndex>();	
	}
	
	public static void renewAllIndexes() {
		for (IndexId indexId : availableIndexes.keySet()) {
			newIndex(indexId);
		}
	}
	
	private MemoryIndex() {
		this(IndexId.getDefaultIndexId());
	}
	
	private MemoryIndex(IndexId indexId) {
		this.indexId = indexId;
	}

	public static MemoryIndex getDefaultIndex() {		
		return getIndex(IndexId.getDefaultIndexId());
	}
	
	public static MemoryIndex getIndex(IndexId indexId) {
		if(availableIndexes.containsKey(indexId))
			return availableIndexes.get(indexId);
		else 
			return newIndex(indexId);		
	}

	public static MemoryIndex newDefaultIndex() {
		return newIndex(IndexId.getDefaultIndexId());
	}
	
	public static MemoryIndex newIndex(IndexId indexId) {
		MemoryIndex newIndex = new MemoryIndex(indexId);
		availableIndexes.put(indexId,newIndex);
		return newIndex;
	}
	
	public IndexId getIndexName() {
		return indexId;
	}
	
	public void addToIndex(Term term, ObjectKey key, PostingMetadata metadata) {
		addToMasterRegistry(key, term);

		PostingList postingList = invertedIndex.get(term);
		
		if(postingList!=null) {
			postingList.add(key, metadata);
		} else {			
			PostingList newPostingList = new PostingList(term);
			newPostingList.add(key, metadata);
			invertedIndex.put(term, newPostingList);
		}
	}

	public boolean contains(Term term) {		
		return invertedIndex.containsKey(term);
	}

	public boolean contains(ObjectKey key) {		
		return masterRegistry.containsKey(key);
	}

	public void removeFromInvertedIndex(Term term) {		
		invertedIndex.remove(term);
	}	
	
	@Override
	public void removePosting(Term term, ObjectKey key) {
		PostingList postingList = invertedIndex.get(term);
		if(postingList!=null) {
			postingList.remove(key);		
			if(postingList.getTermCount()==0) invertedIndex.remove(term);
		}
	}
	
	public void removeFromMasterRegistry(ObjectKey key) {		
		masterRegistry.remove(key);
	}
	
	public void consistentRemove(ObjectKey key, List<Term> termList) {				
		masterRegistry.remove(key);
		
		for (Term term : termList) {
			invertedIndex.get(term).remove(key);
		}
		
		removeFromMasterRegistry(key);
	}
	
	public void consistentRemove(ObjectKey key) {				
		MasterRegistryEntry masterRegistryEntry = masterRegistry.get(key);
		
		if(masterRegistryEntry == null) return;
		
		Set<Term> termSet = masterRegistryEntry.getTerms();
		
		if(termSet == null) return;
		
		for (Term term : termSet) {
			removePosting(term, key);
		}
		
		removeFromMasterRegistry(key);
	}
	
	public PostingList getPostingList(Term term) {		
		return invertedIndex.get(term);
	}

	public int getTermDictionarySize() {
		return invertedIndex.size();
	}

	public Iterator<Term> getTermDictionaryIterator() {
		return invertedIndex.keySet().iterator();
	}

	public Iterator<Entry<ObjectKey,MasterRegistryEntry>> getMasterRegistryReadIterator() {
		return new MasterRegistryReadOnlyIterator(masterRegistry.entrySet().iterator());
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((indexId == null) ? 0 : indexId.hashCode());
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
		MemoryIndex other = (MemoryIndex) obj;
		if (indexId == null) {
			if (other.indexId != null)
				return false;
		} else if (!indexId.equals(other.indexId))
			return false;
		return true;
	}
	
	private void addToMasterRegistry(ObjectKey key, Term term) {
		MasterRegistryEntry masterRegistryEntry = masterRegistry.get(key);
				
		Set<Term> objectTerms ;
		if(masterRegistryEntry == null ) {
			objectTerms = new HashSet<Term>();
			masterRegistry.put(key,new MasterRegistryEntry(objectTerms));
		} else {			
			objectTerms = masterRegistryEntry.getTerms();
		}

		objectTerms.add(term);			
	}

	@Override
	public int getObjectCount() {		
		return masterRegistry.size();
	}
	
//	private void removeTermForObject(ObjectKey key, Term term) {
//		MasterRegistryEntry masterRegistryEntry = masterRegistry.get(key);
//		
//		if(masterRegistryEntry == null) return;
//		
//		Set<Term> objectTerms = masterRegistryEntry.getTerms();
//		
//		if(objectTerms == null) return;
//		
//		objectTerms.remove(term);
//		
//		if(objectTerms.size()==0) masterRegistry.remove(key);
//	}
		
	private class MasterRegistryReadOnlyIterator implements Iterator<Entry<ObjectKey,MasterRegistryEntry>> {

		private final Iterator<Entry<ObjectKey,MasterRegistryEntry>> iterator;

		private Entry<ObjectKey,MasterRegistryEntry> current = null;

		public MasterRegistryReadOnlyIterator( Iterator<Entry<ObjectKey, MasterRegistryEntry>> iterator) {		
			this.iterator = iterator;
		}

		@Override
		public boolean hasNext() {			
			return iterator.hasNext();
		}

		@Override
		public Entry<ObjectKey,MasterRegistryEntry> next() {
			current = iterator.next();
			return current;
		}

		@Override
		public void remove() {
			throw new RuntimeException("Remove not allowed on this iterator");
		}


	}

	public static List<MemoryIndex> getAllIndexes() {
		List<MemoryIndex> indexes = new ArrayList<MemoryIndex>();
		for (IndexId indexId : availableIndexes.keySet()) {
			indexes.add(getIndex(indexId));
		}
		return indexes;
	}

	
}
