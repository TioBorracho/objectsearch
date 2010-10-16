package com.jklas.search.engine.operations;

import com.jklas.search.util.TextLibrary;

public class UpperCaseNoSymbolTextNormalizer {

	private final static char[][] accentedVowelMap = {{'á','é','í','ó','ú','Á','É','Í','Ó','Ú'},{'a','e','i','o','u','A','E','I','O','U'}};
		
	private final String[] exceptions;
	
	private final boolean useExceptions;
	
	public UpperCaseNoSymbolTextNormalizer() {
		this.useExceptions =false;
		this.exceptions = null;
	}
	
	public UpperCaseNoSymbolTextNormalizer(String... exceptions) {
		if(exceptions == null) throw new IllegalArgumentException("Can't work with a null list of exceptions... use default constructor instead");
		this.useExceptions = true;
		this.exceptions = exceptions;
	}

	public String normalizeExpression(String text) {
		
		text = TextLibrary.translate(text, accentedVowelMap);
		
		if(useExceptions)
			return TextLibrary.cleanSymbols(text,exceptions).toUpperCase();
		else
			return TextLibrary.cleanSymbols(text).toUpperCase();
	}
	
}
