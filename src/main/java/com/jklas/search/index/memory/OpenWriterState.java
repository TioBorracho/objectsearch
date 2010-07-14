package com.jklas.search.index.memory;

import com.jklas.search.index.IndexId;
import com.jklas.search.index.ObjectKey;
import com.jklas.search.index.PostingMetadata;
import com.jklas.search.index.Term;

public class OpenWriterState implements IndexWriterState {

	@Override
	public void handleClose(MemoryIndexWriter writer) {
		writer.closeWhenOpen();
		writer.setState(new ClosedWriterState());
	}

	@Override
	public void handleWrite(MemoryIndexWriter writer, Term term, ObjectKey key, PostingMetadata metadata) {
		writer.writeWhenOpen(term, key, metadata);
	}

	@Override
	public void handleDelete(MemoryIndexWriter writer, ObjectKey objectKey) {
		writer.deleteWhenOpen(objectKey);
	}

	@Override
	public void handleOpen(MemoryIndexWriter writer, IndexId indexId) {
		writer.openWhenOpen();
	}
}
