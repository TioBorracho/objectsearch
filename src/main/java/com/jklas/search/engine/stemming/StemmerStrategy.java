package com.jklas.search.engine.stemming;

import com.jklas.search.engine.Language;

public interface StemmerStrategy {

	public Stemmer getStemmer(Language language, StemType stemType);

}
