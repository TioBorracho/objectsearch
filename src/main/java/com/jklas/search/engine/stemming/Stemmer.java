package com.jklas.search.engine.stemming;

import com.jklas.search.index.Term;

/**
 * Interfaz para un stemmer del Search Engine.
 * 
 * @author Julián
 * @since 1.0
 * @date 2009-07-26
 */
public interface Stemmer {

	public static final Stemmer NO_STEMMER = IdentityStemmer.getInstance();
	
	public Term stem(Term original);

}
