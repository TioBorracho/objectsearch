/**
 * Object Search Framework
 *
 * Copyright (C) 2010 Julian Klas
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.jklas.search.engine.operations;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.jklas.search.engine.Language;
import com.jklas.search.index.Term;

public class SingleLanguageStopWordCleaner {

	private Language language ;
	
	private Set<Term> stopWords ;
	
	public SingleLanguageStopWordCleaner() {
		this(Language.UNKOWN_LANGUAGE);
	}

	public SingleLanguageStopWordCleaner(Language language) {
		this(language, new HashSet<Term>() );
	}
	
	public SingleLanguageStopWordCleaner(Set<Term> stopWords) {
		this(Language.UNKOWN_LANGUAGE, stopWords);
	}
	
	public SingleLanguageStopWordCleaner(Language language, Set<Term> stopWords) {
		this.language = language;
		this.stopWords = stopWords;
	}
	
	public void setLanguage(Language language) {
		this.language = language;
	}
	
	public void deleteStopWords(List<Term> tokens) {
		for (Iterator<Term> iterator = tokens.iterator(); iterator.hasNext();) {
			Term token = (Term) iterator.next();
			if(stopWords.contains(token)) iterator.remove();
		}		
	}

	public void setStopWords(Set<Term> stopWords) {
		this.stopWords = stopWords;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SingleLanguageStopWordCleaner other = (SingleLanguageStopWordCleaner) obj;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		return true;
	}
}
