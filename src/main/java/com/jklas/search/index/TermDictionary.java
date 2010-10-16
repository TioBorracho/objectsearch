package com.jklas.search.index;

import java.util.Iterator;

public interface TermDictionary {

	public abstract int getTermDictionarySize();

	public abstract Iterator<Term> getTermDictionaryIterator();

}
