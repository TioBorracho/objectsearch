package com.jklas.search.index.memory;

import com.jklas.search.index.IndexId;
import com.jklas.search.index.PostingList;
import com.jklas.search.index.Term;

public interface IndexReaderState {

    public PostingList handleRead(MemoryIndexReader reader, Term term);
    
    public void handleOpen(MemoryIndexReader reader, IndexId indexId);
    
    public void handleClose(MemoryIndexReader reader);

}
