package com.jklas.search.index.memory;

import com.jklas.search.index.IndexId;
import com.jklas.search.index.ObjectKey;
import com.jklas.search.index.PostingMetadata;
import com.jklas.search.index.Term;


public interface IndexWriterState {

    public void handleWrite(MemoryIndexWriter writer, Term term, ObjectKey key, PostingMetadata metadata);
    
    public void handleOpen(MemoryIndexWriter memoryIndexWriter, IndexId indexId);

    public void handleClose(MemoryIndexWriter writer);
    
    public void handleDelete(MemoryIndexWriter berkeleyIndexWriter, ObjectKey objectKey);
}
