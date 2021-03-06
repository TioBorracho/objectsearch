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
package com.jklas.search.engine;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * This is the representation of the concept of "language"
 * inside this framework
 * 
 * @author Julián Klas
 *
 */
public class Language {

	private final String identifier;

	private static Map<String, Language> idToLangMap; 
	
	public static final Language UNKOWN_LANGUAGE;
	
	public static final String UNKOWN_LANGUAGE_IDENTIFIER = "";
	
	static {
		idToLangMap = new HashMap<String,Language>();
		UNKOWN_LANGUAGE = new Language();
	}
	
	private Language() {
		identifier = "";
		idToLangMap.put(UNKOWN_LANGUAGE_IDENTIFIER, UNKOWN_LANGUAGE);
	}
	
	public Language(String identifier) {
		if(identifier == null || (identifier!=null && identifier.length()==0)) throw new IllegalArgumentException("Can't create a language with a null identifier");
		this.identifier = identifier.toUpperCase();
		idToLangMap.put(this.identifier, this);
	}
	
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
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
		Language other = (Language) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}
	
	public boolean isUnknown() {
		return this == UNKOWN_LANGUAGE;
	}

	public static Language getLanguageById(String id) {
		Language lang = idToLangMap.get(id);
		if(lang==null) return UNKOWN_LANGUAGE;
		else return lang;
	}
}
