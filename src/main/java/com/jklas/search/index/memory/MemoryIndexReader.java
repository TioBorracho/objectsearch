/**
 * Object Search Framework
 *
 * Copyright (C) 2010 Julian Klas
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.jklas.search.index.memory;

import com.jklas.search.index.IndexId;
import com.jklas.search.index.MasterAndInvertedIndex;
import com.jklas.search.index.MasterAndInvertedIndexReader;
import com.jklas.search.index.PostingList;
import com.jklas.search.index.Term;

public class MemoryIndexReader implements MasterAndInvertedIndexReader {

	private MasterAndInvertedIndex index;

	private IndexReaderState state;

	public MemoryIndexReader(IndexId indexId) {
		setState(new ClosedReaderState());

		index = MemoryIndex.getIndex(indexId);
		if(index ==null)
			throw new IllegalArgumentException("Can't read index "+indexId+" since it does not exists");
	}

	public MemoryIndexReader() {
		this( IndexId.getDefaultIndexId() );
	}
	
	@Override
	public PostingList openReadAndClose(IndexId indexId, Term term) {
		state.handleOpen(this,indexId);
		try {
			return state.handleRead(this, term);			
		} finally {
			state.handleClose(this);
		}
	}

	@Override
	public PostingList openReadAndClose(Term term) {
		return openReadAndClose(IndexId.getDefaultIndexId(),term);
	}

	@Override
	public void close() {
		state.handleClose(this);
	}

	@Override
	public void open(IndexId indexId) {
		state.handleOpen(this, indexId);
	}
	
	@Override
	public void open() {
		open( IndexId.getDefaultIndexId() );
	}

	@Override
	public PostingList read(Term term) {
		return state.handleRead(this, term);
	}

	@Override
	public int getIndexedObjectCount() {
		return index.getObjectCount();		
	}

	public void closeWhenOpen() {
		index = null;
	}

	public void openWhenOpen() {
		throw new IllegalStateException("Reader already open");
	}

	public void setState(IndexReaderState newState) {
		this.state = newState;
	}

	public void closeWhenClosed() {
		throw new IllegalStateException("Reader already closed");    }

	public void openWhenClosed(IndexId indexId) {
		this.index = MemoryIndex.getIndex(indexId);
	}

	public void openWhenClosed() {
		openWhenClosed( IndexId.getDefaultIndexId() );
	}
	
	public PostingList readWhenClosed() {
		throw new IllegalStateException("Reader is closed");
	}

	public PostingList readWhenOpen(Term term) {
		return index.getPostingList(term);
	}

	@Override
	public boolean isOpen() {
		return state.getClass().isAssignableFrom(OpenWriterState.class);
	}
}
