package com.jklas.search.index;

public interface IndexReaderFactory {

    public MasterAndInvertedIndexReader getIndexReader();
    
    public MasterAndInvertedIndexReader getIndexReader(IndexId indexId);
    
}
