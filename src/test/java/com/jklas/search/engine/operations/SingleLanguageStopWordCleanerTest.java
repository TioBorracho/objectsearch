package com.jklas.search.engine.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.jklas.search.index.Term;


public class SingleLanguageStopWordCleanerTest {

	@Test
	public void SingleStopWordSingleWordIsRemoved() {
		Set<Term> stopWords = new HashSet<Term>();
		
		stopWords.add(new Term("Para"));
		
		SingleLanguageStopWordCleaner stopWordProcessor = new SingleLanguageStopWordCleaner(stopWords);

		List<Term> tokens = new ArrayList<Term>();
		
		tokens.add(new Term("Para"));
		
		stopWordProcessor.deleteStopWords(tokens);
		
		Assert.assertEquals(0, tokens.size());
	}

	@Test
	public void TwoStopWordSingleWordIsRemoved() {
		Set<Term> stopWords = new HashSet<Term>();
		
		stopWords.add(new Term("Para"));
		stopWords.add(new Term("De"));
		
		SingleLanguageStopWordCleaner stopWordProcessor = new SingleLanguageStopWordCleaner(stopWords);

		List<Term> tokens = new ArrayList<Term>();
		
		tokens.add(new Term("Para"));
		
		stopWordProcessor.deleteStopWords(tokens);
		
		Assert.assertEquals(0, tokens.size());
	}

	@Test
	public void SingleStopWordTwoWordIsRemoved() {
		Set<Term> stopWords = new HashSet<Term>();
		
		stopWords.add(new Term("Para"));
		
		SingleLanguageStopWordCleaner stopWordProcessor = new SingleLanguageStopWordCleaner(stopWords);

		List<Term> tokens = new ArrayList<Term>();
		
		tokens.add(new Term("Para"));
		tokens.add(new Term("De"));
		
		stopWordProcessor.deleteStopWords(tokens);
		
		Assert.assertEquals(1, tokens.size());
	}

	@Test
	public void ZeroStopWordDoesntRemovesAnything() {
		Set<Term> stopWords = new HashSet<Term>();
		
		SingleLanguageStopWordCleaner stopWordProcessor = new SingleLanguageStopWordCleaner(stopWords);

		List<Term> tokens = new ArrayList<Term>();
		
		tokens.add(new Term("Para"));
		
		stopWordProcessor.deleteStopWords(tokens);
		
		Assert.assertEquals(1, tokens.size());
	}
	
	@Test
	public void EmptyTermListIsOk() {
		Set<Term> stopWords = new HashSet<Term>();

		stopWords.add(new Term("Para"));
		
		SingleLanguageStopWordCleaner stopWordProcessor = new SingleLanguageStopWordCleaner(stopWords);

		List<Term> tokens = new ArrayList<Term>();
		
		stopWordProcessor.deleteStopWords(tokens);
		
		Assert.assertEquals(0, tokens.size());
	}
	
}
