package com.jklas.search.engine.operations;

import java.util.List;
import java.util.Set;

import com.jklas.search.engine.Language;
import com.jklas.search.index.Term;

public interface StopWordCleaner {

	public void setStopWords(Language language, Set<Term> stopWords);
	
	public void deleteStopWords(Language language, List<Term> tokens);
	
}
