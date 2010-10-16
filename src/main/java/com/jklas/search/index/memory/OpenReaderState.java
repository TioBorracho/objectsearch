package com.jklas.search.index.memory;

import com.jklas.search.index.IndexId;
import com.jklas.search.index.PostingList;
import com.jklas.search.index.Term;

public class OpenReaderState implements IndexReaderState {

    @Override
    public void handleClose(MemoryIndexReader reader) {
	reader.closeWhenOpen();
	reader.setState(new ClosedReaderState());
    }

    @Override
    public PostingList handleRead(MemoryIndexReader reader, Term term) {
	return reader.readWhenOpen(term);
    }

	@Override
	public void handleOpen(MemoryIndexReader reader, IndexId indexId) {
		reader.openWhenOpen();
	}
}
