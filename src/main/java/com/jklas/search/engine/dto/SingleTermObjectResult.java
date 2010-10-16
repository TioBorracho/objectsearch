package com.jklas.search.engine.dto;

import java.lang.reflect.Field;
import java.util.Map;

import com.jklas.search.index.ObjectKey;
import com.jklas.search.index.PostingMetadata;
import com.jklas.search.index.Term;

/**
 * This object is internal to the search
 * engine and encapsulates index results
 * 
 * @author Julián Klas
 * @since 1.0
 * @date 2009-07-26
 *
 */
public class SingleTermObjectResult extends ObjectResult {

    private final Term term;
    
    private final PostingMetadata metadata;
	
    public SingleTermObjectResult(Term term, ObjectKey objectKey, PostingMetadata metadata, Map<Field, Object> storedFields) {
		super(objectKey, storedFields);
		this.term = term;
		this.metadata = metadata;
	}
    
	public SingleTermObjectResult(Term term, ObjectKey objectKey, PostingMetadata metadata) {
		super(objectKey);
		this.term = term;
		this.metadata = metadata;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		ObjectKey objectKey = getKey();
		result = prime * result + ((objectKey == null) ? 0 : objectKey.hashCode());
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
		SingleTermObjectResult other = (SingleTermObjectResult) obj;
		ObjectKey objectKey = getKey();
		if (objectKey == null) {
			if (other.getKey() != null)
				return false;
		} else if (!objectKey.equals(other.getKey())) {
			return false;			
		} else {
			Term thisTerm = getTerm();
			if(thisTerm==null) {
				if(other.getTerm()!=null) return false;
			} else if(!thisTerm.equals(other.getTerm())) return false;
		}
		return true;
	}
	
	public Term getTerm() {
	    return term;
	}
	
	public PostingMetadata getMetadata() {
	    return metadata;
	}
	
	@Override
	public String toString() {
	    return "Term: "+getTerm()+"\n"+super.toString() + "\nMetadata: "+ getMetadata();
	}
}
