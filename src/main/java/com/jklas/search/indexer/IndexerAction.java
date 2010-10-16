package com.jklas.search.indexer;

import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.index.dto.IndexObjectDto;

public enum IndexerAction {
	CREATE, CREATE_OR_UPDATE, DELETE, UPDATE;

	public void execute(IndexerService indexerService, IndexObjectDto objectDto) throws IndexObjectException {
		switch(this) {
			case CREATE: indexerService.create(objectDto); break;
			case CREATE_OR_UPDATE: indexerService.createOrUpdate(objectDto); break;
			case DELETE: indexerService.delete(objectDto); break;
			case UPDATE: indexerService.update(objectDto); break;
		}
	}
	
	
}
