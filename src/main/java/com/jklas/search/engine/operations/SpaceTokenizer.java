package com.jklas.search.engine.operations;

import java.util.List;

import com.jklas.search.index.Term;
import com.jklas.search.util.TextLibrary;

public class SpaceTokenizer {

	public List<Term> tokenize(String text) {
		return TextLibrary.tokenize(text);
	}
	
}
