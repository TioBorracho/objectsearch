
package com.jklas.search.engine.stemming.snowball;

import com.jklas.search.engine.stemming.Stemmer;
import com.jklas.search.index.Term;


public abstract class SnowballStemmer extends SnowballProgram implements Stemmer {
    
	public abstract boolean internalStemming();
         
    @Override
    public Term stem(Term original) {
    	setCurrent(original.getValue().toLowerCase());
    	internalStemming();
    	return new Term(getCurrent().toUpperCase());
    }
};
