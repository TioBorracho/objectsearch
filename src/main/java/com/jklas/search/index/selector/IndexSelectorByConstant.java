package com.jklas.search.index.selector;

import com.jklas.search.exception.SearchEngineException;
import com.jklas.search.index.IndexId;

public class IndexSelectorByConstant extends IndexSelector {

	private String selectedIndex;
	
	public IndexSelectorByConstant(String selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	
	@Override
	public IndexId selectIndex(Object object) throws SearchEngineException {		
		return new IndexId(selectedIndex);
	}

}
