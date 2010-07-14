package com.jklas.search.engine.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jklas.search.configuration.MappedFieldDescriptor;
import com.jklas.search.engine.Language;
import com.jklas.search.engine.operations.NoWordCleaner;
import com.jklas.search.engine.operations.SpaceTokenizer;
import com.jklas.search.engine.operations.StemmingOperation;
import com.jklas.search.engine.operations.UpperCaseNoSymbolTextNormalizer;
import com.jklas.search.index.Term;

public class DefaultTextProcessor implements ObjectTextProcessor {

	private static final NoWordCleaner noWordProcessor = new NoWordCleaner();

	private static final UpperCaseNoSymbolTextNormalizer textNormalizer = new UpperCaseNoSymbolTextNormalizer("+");

	private static final SpaceTokenizer tokenizer = new SpaceTokenizer();

	private static final StemmingOperation stemmingOperation = new StemmingOperation();

	public List<Term> processField(String extractedText, MappedFieldDescriptor fieldDescriptor) {

		Language language = fieldDescriptor.getLanguage();

		String normalizedText = textNormalizer.normalizeExpression(extractedText);

		List<Term> tokens = tokenizer.tokenize(normalizedText);

		noWordProcessor.deleteStopWords(tokens, language);

		if(tokens.size() == 0) Collections.emptyList();

		List<Term> fieldTokens = new ArrayList<Term>();

		for (Term token : tokens) {

			Term stemmedToken = stemmingOperation.applyStemming(token, language, fieldDescriptor.getStemmerStrategy(), fieldDescriptor.getStemType());

			fieldTokens.add(stemmedToken);
		}

		return fieldTokens;		
	}

}
