package com.jklas.search.engine.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.jklas.search.engine.Language;
import com.jklas.search.index.Term;


public class MultiLanguageStopWordCleanerTest {

	@Test
	public void SingleStopWordSingleWordIsRemoved() {
		Set<Term> stopWords = new HashSet<Term>();
		
		stopWords.add(new Term("Para"));
		
		MultiLanguageStopWordCleaner stopWordProcessor = new MultiLanguageStopWordCleaner();
		
		Language spanish = new Language("es");
		stopWordProcessor.setStopWords(spanish, stopWords);

		List<Term> tokens = new ArrayList<Term>();
		
		tokens.add(new Term("Para"));
		
		stopWordProcessor.deleteStopWords(spanish, tokens);
		
		Assert.assertEquals(0, tokens.size());
	}
	
	@Test
	public void DefaultDoesntMakesAnyTrouble() {
		MultiLanguageStopWordCleaner stopWordProcessor = new MultiLanguageStopWordCleaner();
		
		Language spanish = new Language("es");

		List<Term> tokens = new ArrayList<Term>();
		
		tokens.add(new Term("Para"));
		
		stopWordProcessor.deleteStopWords(spanish, tokens);
		
		Assert.assertEquals(1, tokens.size());
	}
	
	@Test
	public void TwoLangRemovalWorksFine() {
		Set<Term> spanishStopWords = new HashSet<Term>();
		spanishStopWords.add(new Term("Para"));
		
		Set<Term> englishStopWords = new HashSet<Term>();
		englishStopWords.add(new Term("For"));
		
		Language spanish = new Language("es");
		Language english= new Language("en");

		MultiLanguageStopWordCleaner stopWordProcessor = new MultiLanguageStopWordCleaner();
		
		stopWordProcessor.setStopWords(english, englishStopWords);
		stopWordProcessor.setStopWords(spanish, spanishStopWords);

		List<Term> tokens = new ArrayList<Term>();
		
		tokens.add(new Term("Para"));
		stopWordProcessor.deleteStopWords(spanish, tokens);
		Assert.assertEquals(0, tokens.size());
		
		tokens.add(new Term("For"));
		stopWordProcessor.deleteStopWords(english, tokens);
		Assert.assertEquals(0, tokens.size());
	}

}
