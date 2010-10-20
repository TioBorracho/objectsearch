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
package com.jklas.search.query.object;

import com.jklas.search.index.IndexId;
import com.jklas.search.index.ObjectKey;
import com.jklas.search.index.Term;

public class ObjectSearchElement {

	private final Term term;
	
	private final ObjectKey key;

	private final IndexId indexId;
	
	public ObjectSearchElement(IndexId indexId, Term term, ObjectKey key) {
		this.indexId = indexId;
		this.term = term;
		this.key = key;
	}
	
	public Term getTerm() {
		return term;
	}
	
	public IndexId getIndexId() {
		return indexId;
	}
	
	public ObjectKey getKey() {
		return key;
	}

}
