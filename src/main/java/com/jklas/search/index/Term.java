package com.jklas.search.index;

import java.io.Serializable;

public class Term implements Serializable {

	private static final long serialVersionUID = 2243740319541218908L;
	
	private final String value; 
	
	public Term(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public int hashCode() {		
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Term other = (Term) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equalsIgnoreCase(other.value))
			return false;
		
		return true;
	}
	
	@Override
	public String toString() {	
		return getValue().toString();
	}
}
