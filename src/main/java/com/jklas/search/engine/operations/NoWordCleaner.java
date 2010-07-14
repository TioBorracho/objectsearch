package com.jklas.search.engine.operations;

import java.util.List;

import com.jklas.search.engine.Language;
import com.jklas.search.index.Term;
import com.jklas.search.util.TextLibrary;

public class NoWordCleaner {

	public void deleteStopWords(List<Term> tokens, Language language) {
		TextLibrary.cleanStopWords(tokens, language.getIdentifier());
	} 
	
}
