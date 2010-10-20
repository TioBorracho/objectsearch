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

import java.util.List;

public interface MasterAndInvertedIndex extends InvertedIndex, MasterIndex{

	/**
	 * Adds a posting to the inverted index.
	 * 
	 * @param term the word for which the posting will be added
	 * @param posting the added posting
	 */
	public abstract void addToIndex(Term term, ObjectKey posting, PostingMetadata metadata);
	
	/**
	 * Removes a key from the master registry and
	 * from the inverted index.
	 * 
	 * This is the only "remove" method that should be used. 
	 * 
	 * @param key the key of the object to be removed from this index
	 * @param termList 
	 */
	public abstract void consistentRemove(ObjectKey key, List<Term> termList);
	
	public abstract void consistentRemove(ObjectKey key);
	
}
