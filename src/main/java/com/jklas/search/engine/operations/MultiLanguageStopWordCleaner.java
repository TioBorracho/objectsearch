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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jklas.search.engine.Language;
import com.jklas.search.index.Term;

public class MultiLanguageStopWordCleaner implements StopWordCleaner {

	private Map<Language, SingleLanguageStopWordCleaner> singleLangProcessors = new HashMap<Language, SingleLanguageStopWordCleaner>();
	
	public void setStopWords(Language language, Set<Term> stopWords) {
		singleLangProcessors.put(language,new SingleLanguageStopWordCleaner(language, stopWords));
	}

	public void deleteStopWords(Language language, List<Term> tokens) {
		SingleLanguageStopWordCleaner cleaner = singleLangProcessors.get(language);
		
		if(cleaner != null) {
			cleaner.deleteStopWords(tokens);
		}
	}
	
	
}
