package com.jklas.search.index;


public interface MasterAndInvertedIndexReader {
        
	public abstract void open();
	
	public abstract void open(IndexId indexId);

	public abstract void close();
	
	public abstract PostingList openReadAndClose(Term term);
	
	public abstract PostingList read(Term term);
	
	public abstract int getIndexedObjectCount();

	PostingList openReadAndClose(IndexId indexId, Term term);

	public abstract boolean isOpen();
}
