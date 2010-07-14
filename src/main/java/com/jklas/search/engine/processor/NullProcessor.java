package com.jklas.search.engine.processor;

import java.util.List;

import com.jklas.search.configuration.MappedFieldDescriptor;
import com.jklas.search.engine.Language;
import com.jklas.search.index.Term;

public class NullProcessor implements ObjectTextProcessor, QueryTextProcessor {
	@Override
	public List<Term> processField(String extractedText,MappedFieldDescriptor fieldDescriptor) {
		throw new RuntimeException("The null ObjectTextProcessor shouldn't be used!");
	}
	

	@Override
	public List<Term> processText(String text, Language language) {
		throw new RuntimeException("The null QueryTextProcessor shouldn't be used!");
	}
}
