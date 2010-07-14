package com.jklas.search.index.selector;

import com.jklas.search.exception.SearchEngineException;
import com.jklas.search.index.IndexId;

public abstract class IndexSelector {

	public abstract IndexId selectIndex(Object object) throws SearchEngineException;
		
}
