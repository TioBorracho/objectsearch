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

import java.util.Iterator;
import java.util.Map.Entry;

public interface MasterIndex {

	/**
	 * Removes a key from the master registry.
	 * 
	 * Usage of this method is discouraged since
	 * it doesn't checks if this key is referenced
	 * in the inverted index (due to the complexity of this operation).
	 * This implies that the inverted index isn't updated as you
	 * would expect.
	 * 
	 * Use at your own risk.
	 * 
	 * @param key the key to be evicted from the master registry
	 */
	public abstract void removeFromMasterRegistry(ObjectKey key);

	public abstract Iterator<Entry<ObjectKey, MasterRegistryEntry>> getMasterRegistryReadIterator();

	public abstract int getObjectCount();
}
