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
package com.jklas.search.index;

import com.jklas.search.index.dto.IndexObjectDto;

public interface IndexWriter {

    public abstract void open();
    
    public abstract void open(IndexId indexId);
    
    public abstract void close();

    public abstract void write(Term term, ObjectKey key, PostingMetadata metadata);

    public abstract void openDeleteAndClose(IndexObjectDto indexObjectDto);
    
    public void openWriteAndClose(Term term, ObjectKey key, PostingMetadata metadata);
    
    public void openWriteAndClose(IndexId indexName, Term term, ObjectKey key, PostingMetadata metadata);

	public void delete(IndexObjectDto indexObjectDto);

}
