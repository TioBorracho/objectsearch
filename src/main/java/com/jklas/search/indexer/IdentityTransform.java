package com.jklas.search.indexer;

public final class IdentityTransform extends Transform<Object> {

	private final static IdentityTransform instance = new IdentityTransform();
	
	private IdentityTransform(){}

	public static IdentityTransform getInstance() {
		return instance;
	}
	
	@Override
	public String transform(Object e) {		
		return e.toString();
	}
	
}
