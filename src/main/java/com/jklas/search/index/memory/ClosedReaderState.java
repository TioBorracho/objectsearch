package com.jklas.search.index.memory;

import com.jklas.search.index.IndexId;
import com.jklas.search.index.PostingList;
import com.jklas.search.index.Term;

public class ClosedReaderState implements IndexReaderState {

	@Override
	public void handleClose(MemoryIndexReader reader) {
		reader.closeWhenClosed();
	}

	@Override
	public PostingList handleRead(MemoryIndexReader reader, Term term) {
		return reader.readWhenClosed();
	}

	@Override
	public void handleOpen(MemoryIndexReader reader, IndexId indexId) {
		reader.openWhenClosed(indexId);
		reader.setState(new OpenReaderState());
	}


}
