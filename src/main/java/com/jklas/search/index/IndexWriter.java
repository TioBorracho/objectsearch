package com.jklas.search.index;

import com.jklas.search.index.dto.IndexObjectDto;

public interface IndexWriter {

    public abstract void open();
    
    public abstract void open(IndexId indexId);
    
    public abstract void close();

    public abstract void write(Term term, ObjectKey key, PostingMetadata metadata);

    public abstract void openDeleteAndClose(IndexObjectDto indexObjectDto);
    
    public void openWriteAndClose(Term term, ObjectKey key, PostingMetadata metadata);
    
    public void openWriteAndClose(IndexId indexName, Term term, ObjectKey key, PostingMetadata metadata);

	public void delete(IndexObjectDto indexObjectDto);

}
