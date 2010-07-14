package com.jklas.search.engine.processor;

import java.util.Arrays;
import java.util.List;

import com.jklas.search.configuration.MappedFieldDescriptor;
import com.jklas.search.engine.Language;
import com.jklas.search.index.Term;

public class OneTermTextProcessor implements ObjectTextProcessor, QueryTextProcessor {

	@Override
	public List<Term> processField(String extractedText, MappedFieldDescriptor fieldDescriptor) {
		return Arrays.asList(new Term(extractedText));
	}

	@Override
	public List<Term> processText(String text, Language language) {
		return Arrays.asList(new Term(text));		
	}


}
