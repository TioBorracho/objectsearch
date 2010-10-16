package com.jklas.search.indexer.online;

import java.util.List;

import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.index.dto.IndexObjectDto;
import com.jklas.search.indexer.IndexerService;
import com.jklas.search.util.SearchLibrary;

public class OnlineIndexer implements IndexerService {

	private final IndexerService indexerService;

	public OnlineIndexer(IndexerService indexerService) {
		this.indexerService = indexerService;
	}

	public void create(IndexObjectDto indexObjectDto) throws IndexObjectException {
		indexerService.create(indexObjectDto);
	}

	@Override
	public void create(Object entity) throws IndexObjectException {
		indexerService.create(entity);
	}

	@Override
	public void createOrUpdate(Object entity) throws IndexObjectException {
		indexerService.createOrUpdate(entity);
	}

	@Override
	public void createOrUpdate(IndexObjectDto indexObjectDto) throws IndexObjectException {
		indexerService.createOrUpdate(indexObjectDto);
	}

	@Override
	public void delete(Object entity) throws IndexObjectException {
		indexerService.delete(entity);
	}

	@Override
	public void delete(IndexObjectDto indexObjectDto) throws IndexObjectException {
		indexerService.delete(indexObjectDto);
	}

	@Override
	public void update(Object entity) throws IndexObjectException {
		indexerService.update(entity);
	}

	@Override
	public void update(IndexObjectDto indexObjectDto) throws IndexObjectException {
		indexerService.update(indexObjectDto);
	}

	@Override
	public void bulkCreate(List<?> entities) throws IndexObjectException {		
		for (Object entity : entities) {
			create(entity);
		}
	}

	@Override
	public void bulkCreateOrUpdate(List<?> entities) throws IndexObjectException {
		for (Object entity : entities) {
			createOrUpdate(entity);
		}
	}

	@Override
	public void bulkDelete(List<?> entities) throws IndexObjectException {
		for (Object entity : entities) {
			delete(entity);
		}
	}

	@Override
	public void bulkDtoCreate(List<IndexObjectDto> indexObjectDto) throws IndexObjectException {
		bulkCreate(SearchLibrary.convertDtoListToEntityList(indexObjectDto));
	}

	@Override
	public void bulkDtoCreateOrUpdate(List<IndexObjectDto> indexObjectDto) throws IndexObjectException {
		bulkCreateOrUpdate(SearchLibrary.convertDtoListToEntityList(indexObjectDto));
	}

	@Override
	public void bulkDtoDelete(List<IndexObjectDto> indexObjectDto) throws IndexObjectException {
		bulkDelete(SearchLibrary.convertDtoListToEntityList(indexObjectDto));
	}

	@Override
	public void bulkDtoUpdate(List<IndexObjectDto> indexObjectDto) throws IndexObjectException {
		bulkUpdate(SearchLibrary.convertDtoListToEntityList(indexObjectDto));
	}

	@Override
	public void bulkUpdate(List<?> entities) throws IndexObjectException {
		for (Object entity : entities) {
			update(entity);
		}
	}

}
