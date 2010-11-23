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
package com.jklas.search.interceptors;

import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.index.dto.IndexObject;
import com.jklas.search.indexer.IndexerService;

/**
 * 
 * Esto se ejecuta en la transacción de negocio.
 * 
 * @author Julián
 *
 */
public class SearchInterceptor {
	
	private IndexerService indexerService;
	
	@SuppressWarnings("unused")
	private SearchInterceptor() {}
	
	public SearchInterceptor(IndexerService indexerService) {
		this.indexerService = indexerService;
	}
	
	public void setIndexerService(IndexerService indexerService) {
		this.indexerService = indexerService;
	}
	
	public IndexerService getIndexerService() {
		return indexerService;
	}
	
	public void createOrUpdate(IndexObject indexObjectDto) throws IndexObjectException {
		try {
			indexerService.createOrUpdate(indexObjectDto);
		} catch (IndexObjectException e) {
			throw new IndexObjectException("Error al indexar "+indexObjectDto,e);
		}		
	}
		
	public void delete(IndexObject indexObjectDto ) throws IndexObjectException {		
		try {
			indexerService.delete(indexObjectDto);
		} catch (IndexObjectException e) {
			throw new IndexObjectException("Error al indexar "+indexObjectDto,e);
		}
	}

	public void update(IndexObject indexObjectDto) throws IndexObjectException {
		try {
			indexerService.update(indexObjectDto);
		} catch (IndexObjectException e) {
			throw new IndexObjectException("Error al indexar "+indexObjectDto,e);
		}
	}
	
	public void create(IndexObject indexObjectDto) throws IndexObjectException {		
		try {
			indexerService.create(indexObjectDto);
		} catch (IndexObjectException e) {
			throw new IndexObjectException("Error al indexar "+indexObjectDto,e);
		}
	}
}
