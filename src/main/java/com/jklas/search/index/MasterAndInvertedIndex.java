package com.jklas.search.index;

import java.util.List;

public interface MasterAndInvertedIndex extends InvertedIndex, MasterIndex{

	/**
	 * Adds a posting to the inverted index.
	 * 
	 * @param term the word for which the posting will be added
	 * @param posting the added posting
	 */
	public abstract void addToIndex(Term term, ObjectKey posting, PostingMetadata metadata);
	
	/**
	 * Removes a key from the master registry and
	 * from the inverted index.
	 * 
	 * This is the only "remove" method that should be used. 
	 * 
	 * @param key the key of the object to be removed from this index
	 * @param termList 
	 */
	public abstract void consistentRemove(ObjectKey key, List<Term> termList);
	
	public abstract void consistentRemove(ObjectKey key);
	
}
