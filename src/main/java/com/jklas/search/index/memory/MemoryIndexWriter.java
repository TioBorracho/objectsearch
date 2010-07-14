package com.jklas.search.index.memory;

import com.jklas.search.index.IndexId;
import com.jklas.search.index.IndexWriter;
import com.jklas.search.index.ObjectKey;
import com.jklas.search.index.PostingMetadata;
import com.jklas.search.index.Term;
import com.jklas.search.index.dto.IndexObjectDto;

public class MemoryIndexWriter implements IndexWriter {

	private IndexWriterState state = new NewWriterState();

	private MemoryIndex index;

	@Override
	public void close() {
		state.handleClose(this);
	}

	@Override
	public void openWriteAndClose(IndexId indexId, Term term, ObjectKey key, PostingMetadata metadata) {
		state.handleOpen(this, indexId);
		try {
			state.handleWrite(this, term, key, metadata);
		} finally {
			state.handleClose(this);
		}
	}

	@Override
	public void write(Term term, ObjectKey key, PostingMetadata metadata) {
		state.handleWrite(this, term, key, metadata);
	}

	@Override
	public void openDeleteAndClose(IndexObjectDto indexObjectDto) {
		state.handleOpen(this, indexObjectDto.getIndexId());
		try {
			ObjectKey oik = new ObjectKey(indexObjectDto.getEntity().getClass(), indexObjectDto.getId());
			state.handleDelete(this, oik);
		} finally {
			state.handleClose(this);
		}		
	}

	@Override
	public void openWriteAndClose(Term term, ObjectKey key, PostingMetadata metadata) {
		openWriteAndClose(IndexId.getDefaultIndexId(), term, key, metadata);
	}
	
	@Override
	public void open() {
		state.handleOpen(this, IndexId.getDefaultIndexId());
	}

	@Override
	public void open(IndexId indexId) {
		state.handleOpen(this,indexId);
	}

	public void closeWhenOpen() {
		index = null;
	}

	public void closeWhenClosed() {
		throw new IllegalStateException("Reader already closed");
	}

	public void deleteWhenClosed() {
		throw new IllegalStateException("Reader is closed");	
	}

	public void openWhenClosed() {
		throw new IllegalStateException("Closed readers can't be opened again... you must create a new one");
	}

	public void setState(IndexWriterState newState) {
		this.state = newState;	
	}

	public void writeWhenClosed() {
		throw new IllegalStateException("Reader is closed");
	}

	public void openWhenOpen() {
		throw new IllegalStateException("Reader already open");
	}

	public void writeWhenOpen(Term term, ObjectKey key, PostingMetadata metadata) {
		index.addToIndex(term, key, metadata);
	}

	public void deleteWhenOpen(ObjectKey objectKey) {
		index.consistentRemove(objectKey);
	}

	public void openWhenOpen(IndexId indexId) {
		throw new IllegalStateException("Reader already open");
	}

	public void closeWhenNew() {
		throw new IllegalStateException("Reader can't be closed since it was never open");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((index == null) ? 0 : index.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MemoryIndexWriter other = (MemoryIndexWriter) obj;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

	public void deleteWhenNew() {
		throw new IllegalStateException("Reader must be open to delete");
	}

	public void openWhenNew() {
		this.index = MemoryIndex.getDefaultIndex();
	}

	public void writeWhenNew() {
		throw new IllegalStateException("Reader must be open to write");
	}

	public void openWhenNew(IndexId indexId) {
		this.index = MemoryIndex.getIndex(indexId);
	}

	@Override
	public void delete(IndexObjectDto indexObjectDto) {
		ObjectKey oik = new ObjectKey(indexObjectDto.getEntity().getClass(), indexObjectDto.getId());
		state.handleDelete(this, oik);
	}
}
