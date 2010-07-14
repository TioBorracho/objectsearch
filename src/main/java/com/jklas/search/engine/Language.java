package com.jklas.search.engine;

import java.util.HashMap;
import java.util.Map;

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
		identifier = null;
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
