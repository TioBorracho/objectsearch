package com.jklas.search.engine.processor;

import java.util.List;

import com.jklas.search.configuration.MappedFieldDescriptor;
import com.jklas.search.index.Term;

public interface ObjectTextProcessor {

	public static final Class<? extends ObjectTextProcessor> DFLT_TEXT_PROCESSOR = DefaultTextProcessor.class;
	
	public static final Class<? extends ObjectTextProcessor> NULL_PROCESSOR = NullProcessor.class;
	
	public abstract List<Term> processField(String extractedText, MappedFieldDescriptor fieldDescriptor);

}
