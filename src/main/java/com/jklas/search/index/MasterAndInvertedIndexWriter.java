package com.jklas.search.index;


public interface MasterAndInvertedIndexWriter {

    public abstract void open();
    
    public abstract void open(IndexId indexId);
    
    public abstract void close();

    public abstract void write(Term term, ObjectKey key, PostingMetadata metadata);
    
    void openDeleteAndClose(IndexId indexId, ObjectKey objectKey);

    public void openWriteAndClose(Term term, ObjectKey key, PostingMetadata metadata);
    
    public void openWriteAndClose(IndexId indexName, Term term, ObjectKey key, PostingMetadata metadata);
    
    public void delete(ObjectKey objectKey);

}
