/**
 * Object Search Framework
 *
 * Copyright (C) 2010 Julian Klas
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.jklas.search.indexer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jklas.search.SearchEngine;
import com.jklas.search.configuration.SearchConfiguration;
import com.jklas.search.configuration.SearchMapping;
import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.exception.SearchEngineException;
import com.jklas.search.exception.SearchEngineMappingException;
import com.jklas.search.index.IndexId;
import com.jklas.search.index.MasterAndInvertedIndexWriter;
import com.jklas.search.index.IndexWriterFactory;
import com.jklas.search.index.ObjectKey;
import com.jklas.search.index.PostingMetadata;
import com.jklas.search.index.Term;
import com.jklas.search.index.dto.IndexObject;
import com.jklas.search.indexer.pipeline.IndexingPipeline;
import com.jklas.search.indexer.pipeline.SemiIndex;
import com.jklas.search.util.SearchLibrary;

public class DefaultIndexerService implements IndexerService {

	private IndexingPipeline indexingPipeline;

	private final IndexWriterFactory writerFactory;

	public DefaultIndexerService(IndexingPipeline indexingPipeline, IndexWriterFactory factory) {
		setIndexingPipeline(indexingPipeline);
		this.writerFactory = factory;
	}

	@Override
	public void create(Object entity) throws IndexObjectException {
		LinkedList<Object> linkedList = new LinkedList<Object>();
		linkedList.add(entity);
		bulkCreate(linkedList);
	}


	@Override
	public void createOrUpdate(Object entity) throws IndexObjectException {
		delete(entity);
		create(entity);
	}

	@Override
	public void delete(Object entity) throws IndexObjectException {

		SearchConfiguration configuration = SearchEngine.getInstance().getConfiguration();

		Class<?> clazz = entity.getClass();

		if(!configuration.isMapped(clazz)) throw new IndexObjectException("Can't delete object since class "+clazz+" isn't mapped");

		SearchMapping mapping = configuration.getMapping(entity.getClass());

		try {
			Serializable id = (Serializable)mapping.extractId(entity);
			IndexId indexId = mapping.getIndexSelector().selectIndex(entity);
			writerFactory.getIndexWriter().openDeleteAndClose(indexId, new ObjectKey(entity.getClass(), id));
		} catch (SearchEngineException e) {
			throw new RuntimeException("Couldn't get object id or index id",e);
		} catch (SearchEngineMappingException e) {
			throw new RuntimeException("Couldn't get object id",e);
		}
	}

	@Override
	public void update(Object entity) throws IndexObjectException {
		delete(entity);
		create(entity);
	}

	public void setIndexingPipeline(IndexingPipeline indexingPipeline) {
		this.indexingPipeline = indexingPipeline;
	}

	@Override
	public void create(IndexObject indexObjectDto) throws IndexObjectException {
		nullCheck(indexObjectDto);
		create(indexObjectDto.getEntity());		
	}

	@Override
	public void createOrUpdate(IndexObject indexObjectDto) throws IndexObjectException {
		nullCheck(indexObjectDto);
		createOrUpdate(indexObjectDto.getEntity());		
	}

	@Override
	public void delete(IndexObject indexObjectDto) throws IndexObjectException {
		nullCheck(indexObjectDto);
		delete(indexObjectDto.getEntity());		
	}

	@Override
	public void update(IndexObject indexObjectDto) throws IndexObjectException {
		nullCheck(indexObjectDto);
		update(indexObjectDto.getEntity());		
	}

	private void nullCheck(IndexObject indexObjectDto) throws IndexObjectException {
		if(indexObjectDto == null) throw new IndexObjectException("Can't index a null entity");
	}

	@Override
	public void bulkCreate(List<?> entities) throws IndexObjectException {
		HashMap<IndexId, MasterAndInvertedIndexWriter> openWriters = new HashMap<IndexId, MasterAndInvertedIndexWriter>();
		try {
			for (Object entity : entities) {
				if(entity == null) throw new IndexObjectException("Can't index null entities");

				SemiIndex semiIndex = indexingPipeline.processObject(entity);

				for (Entry<IndexObject, Map<Term,PostingMetadata>> semiIndexEntry: semiIndex.getSemiIndexMap().entrySet()) {
					IndexObject current = semiIndexEntry.getKey();

					IndexId currentIndexId = current.getIndexId();
					MasterAndInvertedIndexWriter writer ;
					if(openWriters.containsKey(currentIndexId)) writer = openWriters.get(currentIndexId);
					else {
						writer = writerFactory.getIndexWriter();
						openWriters.put(currentIndexId, writer);
						writer.open(currentIndexId);
					}

					Map<Term,PostingMetadata> termPostingMap = semiIndexEntry.getValue();
					Class<?> currentObjectClass = current.getEntity().getClass();
					Serializable currentObjectId = current.getId();

					for (Map.Entry<Term, PostingMetadata> entry: termPostingMap.entrySet()) {
						Term term = entry.getKey();
						ObjectKey key = new ObjectKey(currentObjectClass,currentObjectId);

						writer.write(term, key, entry.getValue());
					}
				}
			}
		} finally {
			for (MasterAndInvertedIndexWriter writer : openWriters.values()) {
				writer.close();
			}
		}
	}

	@Override
	public void bulkCreateOrUpdate(List<?> entity) throws IndexObjectException {
		bulkDelete(entity);
		bulkCreate(entity);
	}

	@Override
	public void bulkDelete(List<?> entities) throws IndexObjectException {
		MasterAndInvertedIndexWriter indexWriter = writerFactory.getIndexWriter();

		try {
			for (Object entity: entities) {	
				IndexObject indexObjectDto = new IndexObject(entity);
				indexWriter.delete(new ObjectKey(indexObjectDto.getClass(), indexObjectDto.getId()));
			}
		} finally {
			indexWriter.close();
		}
	}

	@Override
	public void bulkDtoCreate(List<IndexObject> indexObjectDto) throws IndexObjectException {
		List<Object> entities = SearchLibrary.convertDtoListToEntityList(indexObjectDto);

		bulkCreate(entities);
	}

	@Override
	public void bulkDtoCreateOrUpdate(List<IndexObject> indexObjectDto) throws IndexObjectException {
		bulkDtoDelete(indexObjectDto);
		bulkDtoCreate(indexObjectDto);
	}

	@Override
	public void bulkDtoDelete(List<IndexObject> indexObjectDto) throws IndexObjectException {
		List<Object> entities = SearchLibrary.convertDtoListToEntityList(indexObjectDto);		
		bulkDelete(entities);
	}



	@Override
	public void bulkDtoUpdate(List<IndexObject> indexObjectDto) throws IndexObjectException {
		List<Object> entities = SearchLibrary.convertDtoListToEntityList(indexObjectDto);		
		bulkUpdate(entities);
	}

	@Override
	public void bulkUpdate(List<?> entities) throws IndexObjectException {
		bulkDelete(entities);
		bulkCreate(entities);
	}
}
