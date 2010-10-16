package com.jklas.search.index.selector;

import com.jklas.search.exception.SearchEngineException;
import com.jklas.search.index.IndexId;

public class IndexSelectorByClassName extends IndexSelector {

	@Override
	public IndexId selectIndex(Object object) throws SearchEngineException {
		return new IndexId(object.getClass().getName());
	}


}
