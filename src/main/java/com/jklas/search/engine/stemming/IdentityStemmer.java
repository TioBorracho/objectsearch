package com.jklas.search.engine.stemming;

import com.jklas.search.index.Term;

public final class IdentityStemmer implements Stemmer {

	private static final IdentityStemmer instance = new IdentityStemmer();
	
	public static IdentityStemmer getInstance() {
		return instance;
	}
	
	private IdentityStemmer(){}
	
	@Override
	public Term stem(Term original) {
		return original;
	}

}
