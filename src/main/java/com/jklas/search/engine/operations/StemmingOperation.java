package com.jklas.search.engine.operations;

import com.jklas.search.engine.Language;
import com.jklas.search.engine.stemming.StemType;
import com.jklas.search.engine.stemming.StemmerStrategy;
import com.jklas.search.index.Term;

public class StemmingOperation {

	public Term applyStemming(Term token, Language language, StemmerStrategy stemStrategy, StemType stemType) {		
		if(stemType == StemType.NO_STEM) return token;

		return stemStrategy.getStemmer(language,stemType).stem(token);
	}
	
}
