package com.jklas.search.util;

import junit.framework.Assert;

import org.junit.Test;

public class TextLibraryTest {

	@Test
	public void CleanSymbolsExceptionsTest() {
		
		String[] exceptions = {"+AND","+OR","+NOT",".","á"};
				
		Assert.assertEquals("", TextLibrary.cleanSymbols("", exceptions));
		
		Assert.assertEquals("+AND", TextLibrary.cleanSymbols("+AND", exceptions));
		
		Assert.assertEquals("+OR", TextLibrary.cleanSymbols("+OR", exceptions));
		
		Assert.assertEquals("+NOT", TextLibrary.cleanSymbols("+NOT", exceptions));
		
		Assert.assertEquals("+AND+OR+NOT", TextLibrary.cleanSymbols("+AND+OR+NOT", exceptions));
		
		Assert.assertEquals("+AND+OR+NOT", TextLibrary.cleanSymbols("  +AND+OR+NOT  ", exceptions));
		
		Assert.assertEquals("+ANDDDD", TextLibrary.cleanSymbols("+ANDDDD", exceptions));
		
		Assert.assertEquals("AAND", TextLibrary.cleanSymbols("+AAND", exceptions));
		
		Assert.assertEquals("+AND", TextLibrary.cleanSymbols("++++AND++++", exceptions));
		
		Assert.assertEquals("+OR +AND +NOT", TextLibrary.cleanSymbols("++OR++AND++NOT++", exceptions));
		
		Assert.assertEquals("jklas fi.uba.ar +OR Julián Klas", TextLibrary.cleanSymbols("jklas@fi.uba.ar +OR Julián Klas", exceptions));
	}
	
	@Test
	public void CharTranslationTest() {
		
		char[] from = {'á','é','í','ó','ú','Á','É','Í','Ó','Ú'};
		char[] to = {'a','e','i','o','u','A','E','I','O','U'};
						
		Assert.assertEquals("", TextLibrary.translate("", from, to));
		Assert.assertEquals("ABCDEFGHIJKMNLOPQRSTUVWXYZ", TextLibrary.translate("ABCDEFGHIJKMNLOPQRSTUVWXYZ", from, to));
		Assert.assertEquals("aeiouAEIOU", TextLibrary.translate("áéíóúÁÉÍÓÚ", from, to));
		Assert.assertEquals("jklas@fi.uba.ar +OR Julian Klas", TextLibrary.translate("jklas@fi.uba.ar +OR Julián Klas", from, to));
	}
	
	@Test
	public void CharTranslationMapTest() {
		
		char[][] map = {{'á','é','í','ó','ú','Á','É','Í','Ó','Ú'},{'a','e','i','o','u','A','E','I','O','U'}};
		
						
		Assert.assertEquals("", TextLibrary.translate("", map));
		Assert.assertEquals("ABCDEFGHIJKMNLOPQRSTUVWXYZ", TextLibrary.translate("ABCDEFGHIJKMNLOPQRSTUVWXYZ", map));
		Assert.assertEquals("aeiouAEIOU", TextLibrary.translate("áéíóúÁÉÍÓÚ", map));
		Assert.assertEquals("jklas@fi.uba.ar +OR Julian Klas", TextLibrary.translate("jklas@fi.uba.ar +OR Julián Klas", map));
	}
	
	@Test
	public void CharCleaningTest() {
		Assert.assertEquals("a b", TextLibrary.cleanSymbols("a$b"));
		Assert.assertEquals("ab", TextLibrary.cleanSymbols("ab$"));
		Assert.assertEquals("ab", TextLibrary.cleanSymbols("$ab"));
		Assert.assertEquals("ab", TextLibrary.cleanSymbols("ab"));
	}
	
}
