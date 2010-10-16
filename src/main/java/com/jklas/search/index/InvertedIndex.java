package com.jklas.search.index;



public interface InvertedIndex {

	
	/**
	 * 
	 * Removes a term from the inverted index.
	 * 
	 * Usage of this method is discouraged since
	 * it doesn't checks if this term
	 * holds the last reference to the objects (due to the complexity of this operation).
	 * 
	 * This implies that the master registry
	 * won't get updated when this method is called.
	 * 
	 * Use at your own risk.
	 * 
	 * @param term the term that will be deleted from the inverted index
	 */
	public abstract void removeFromInvertedIndex(Term term);


	/**
	 * Retrieves a complete posting lists from the index
	 *  
	 * @param term the form for which the posting list will be retrieved
	 * @return 
	 */
	public abstract PostingList getPostingList(Term term);
	
	public abstract int getTermDictionarySize();

	public abstract void removePosting(Term term, ObjectKey key);
	
}