package com.jklas.search.engine.stemming;

import com.jklas.search.engine.Language;
import com.jklas.search.engine.stemming.snowball.englishStemmer;

public class EnglishSnowballStemmingStrategy implements StemmerStrategy {
	
	@Override
	public Stemmer getStemmer(Language language, StemType stemType) {
		return new englishStemmer();
	}
}