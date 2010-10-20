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



public interface InvertedIndex {

	
	/**
	 * 
	 * Removes a term from the inverted index.
	 * 
	 * Usage of this method is discouraged since
	 * it doesn't checks if this term
	 * holds the last reference to the objects (due to the complexity of this operation).
	 * 
	 * This implies that the master registry
	 * won't get updated when this method is called.
	 * 
	 * Use at your own risk.
	 * 
	 * @param term the term that will be deleted from the inverted index
	 */
	public abstract void removeFromInvertedIndex(Term term);


	/**
	 * Retrieves a complete posting lists from the index
	 *  
	 * @param term the form for which the posting list will be retrieved
	 * @return 
	 */
	public abstract PostingList getPostingList(Term term);
	
	public abstract int getTermDictionarySize();

	public abstract void removePosting(Term term, ObjectKey key);
	
}