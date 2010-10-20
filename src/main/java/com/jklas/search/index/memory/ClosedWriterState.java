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
