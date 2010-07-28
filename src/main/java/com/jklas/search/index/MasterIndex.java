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
	 * Uste at your own risk.
	 * 
	 * @param key the key to be evicted from the master registry
	 */
	public abstract void removeFromMasterRegistry(ObjectKey key);

	public abstract Iterator<Entry<ObjectKey, MasterRegistryEntry>> getMasterRegistryReadIterator();

	public abstract int getObjectCount();
}
