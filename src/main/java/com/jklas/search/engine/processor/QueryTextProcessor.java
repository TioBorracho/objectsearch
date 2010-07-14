package com.jklas.search.engine.processor;

import java.util.List;

import com.jklas.search.engine.Language;
import com.jklas.search.index.Term;

public interface QueryTextProcessor {

	public static final Class<? extends QueryTextProcessor> DFLT_TEXT_PROCESSOR = NullProcessor.class;
	
	public List<Term> processText(String text, Language language);

}
