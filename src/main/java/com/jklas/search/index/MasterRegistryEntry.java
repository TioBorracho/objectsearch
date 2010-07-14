package com.jklas.search.index;

import java.io.Serializable;
import java.util.Set;

public class MasterRegistryEntry implements Serializable {

	private static final long serialVersionUID = -7742395546952635382L;

	private final Set<Term> terms;

	public MasterRegistryEntry(Set<Term> terms) {
		this.terms = terms;
	}

	public Set<Term> getTerms() {
		return terms;
	}

}