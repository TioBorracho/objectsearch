package com.jklas.search.index;

import java.io.Serializable;

public class IndexId implements Serializable {

	private static final long serialVersionUID = -4357142232120086999L;
	
	public final static String DEFAULT_INDEX_NAME = "";
	
	private final static IndexId DEFAULT_INDEX_ID = new IndexId(DEFAULT_INDEX_NAME);
	
	private final String indexName;	
	
	public IndexId(String indexName) {
		this.indexName = indexName;
	}
	
	public String getIndexName() {
		return indexName;
	}
	
	public static IndexId getDefaultIndexId() {
		return DEFAULT_INDEX_ID;
	}

	@Override
	public int hashCode() {		
		return ((indexName == null) ? 0 : indexName.hashCode());		
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexId other = (IndexId) obj;
		if (indexName == null) {
			if (other.indexName != null)
				return false;
		} else if (!indexName.equals(other.indexName))
			return false;
		return true;
	}
	
	@Override
	public String toString() {	
		return getIndexName();
	}
}
