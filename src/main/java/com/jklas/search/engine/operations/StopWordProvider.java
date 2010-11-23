package com.jklas.search.engine.operations;

import com.jklas.search.engine.Language;

public interface StopWordProvider {
	public void provideStopWords(Language language, StopWordCleaner cleaner);
	
}
