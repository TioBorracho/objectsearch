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
