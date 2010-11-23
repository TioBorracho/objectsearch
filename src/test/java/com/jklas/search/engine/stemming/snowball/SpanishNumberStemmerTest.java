package com.jklas.search.engine.stemming.snowball;

import junit.framework.Assert;

import org.junit.Test;

import com.jklas.search.index.Term;


public class SpanishNumberStemmerTest {

	@Test
	public void StemmerIsCaseInsensitive() {
		SpanishLightStemmer spanishNumberStemmer = new SpanishLightStemmer();
		Term stemFromPlural = spanishNumberStemmer.stem(new Term("trabajos"));
		Term stemFromSingular = spanishNumberStemmer.stem(new Term("trabajo"));
		
		Assert.assertEquals(stemFromPlural , stemFromSingular);
		
		stemFromPlural = spanishNumberStemmer.stem(new Term("trabajos".toUpperCase()));
		stemFromSingular = spanishNumberStemmer.stem(new Term("trabajo".toUpperCase()));
		
		Assert.assertEquals(stemFromPlural , stemFromSingular);
	}
	
}
