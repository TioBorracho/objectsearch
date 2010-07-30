package com.jklas.search.index;

public interface IndexWriterFactory {
    
    public MasterAndInvertedIndexWriter getIndexWriter();
    
}
