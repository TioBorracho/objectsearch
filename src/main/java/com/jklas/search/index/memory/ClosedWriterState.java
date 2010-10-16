package com.jklas.search.index.memory;

import com.jklas.search.index.IndexId;
import com.jklas.search.index.ObjectKey;
import com.jklas.search.index.PostingMetadata;
import com.jklas.search.index.Term;

public class ClosedWriterState implements IndexWriterState {

	@Override
	public void handleClose(MemoryIndexWriter writer) {
		writer.closeWhenClosed();
	}

	@Override
	public void handleDelete(MemoryIndexWriter writer, ObjectKey objectKey) {
		writer.deleteWhenClosed();
	}

	@Override
	public void handleWrite(MemoryIndexWriter writer, Term term, ObjectKey key, PostingMetadata metadata) {
		writer.writeWhenClosed();
	}

	@Override
	public void handleOpen(MemoryIndexWriter writer, IndexId indexId) {
		writer.openWhenOpen(indexId);
	}
}
