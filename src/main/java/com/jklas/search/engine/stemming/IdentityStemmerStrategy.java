package com.jklas.search.engine.stemming;

import com.jklas.search.engine.Language;

public final class IdentityStemmerStrategy implements StemmerStrategy {

	@Override
	public Stemmer getStemmer(Language language, StemType stemType) {
		return IdentityStemmer.getInstance();
	}


}
