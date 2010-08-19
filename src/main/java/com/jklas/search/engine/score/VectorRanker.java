package com.jklas.search.engine.score;

import java.util.List;
import java.util.Set;

import com.jklas.search.engine.dto.SingleTermObjectResult;
import com.jklas.search.engine.dto.VectorRankedResult;
import com.jklas.search.index.MasterAndInvertedIndexReader;
import com.jklas.search.query.vectorial.VectorQuery;

public interface VectorRanker {

	public abstract List<VectorRankedResult> rank(VectorQuery vectorQuery,
			Set<SingleTermObjectResult> unsortedResults,
			MasterAndInvertedIndexReader reader);

}