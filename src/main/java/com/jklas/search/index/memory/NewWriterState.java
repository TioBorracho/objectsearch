package com.jklas.search.index.memory;

import com.jklas.search.index.IndexId;
import com.jklas.search.index.ObjectKey;
import com.jklas.search.index.PostingMetadata;
import com.jklas.search.index.Term;

public class NewWriterState implements IndexWriterState {

	@Override
	public void handleClose(MemoryIndexWriter writer) {
		writer.closeWhenNew();
	}

	@Override
	public void handleDelete(MemoryIndexWriter writer, ObjectKey objectKey) {
		writer.deleteWhenNew();
	}

	@Override
	public void handleWrite(MemoryIndexWriter writer, Term term, ObjectKey key, PostingMetadata metadata) {
		writer.writeWhenNew();
	}

	@Override
	public void handleOpen(MemoryIndexWriter writer, IndexId indexId) {
		writer.openWhenNew(indexId);
		writer.setState(new OpenWriterState());
	}
}
