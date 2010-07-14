package com.jklas.search.query;

public abstract class SearchQuery {

	private int page = 1 ;

	private int pageSize = 10;
	
	public void setPage(int pageNumber) {
		this.page = pageNumber;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public int getPage() {
		return page;
	}
	
	public int getPageSize() {
		return pageSize;
	}
}
