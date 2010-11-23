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

import java.util.List;

import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.index.dto.IndexObject;

/**
 * Interfaz para el servicio de indexación de objetos.
 * 
 * Las implementaciones pueden ser locales, remotas, sincrónicas
 * o asincrónicas.
 * 
 * @author Julián Klas
 * @date 2009-07-26
 * @since 1.0
 *
 */
public interface IndexerService {
	
	public void create(Object entity) throws IndexObjectException;
	public void create(IndexObject indexObjectDto) throws IndexObjectException;	
	
	public void bulkCreate(List<?> entity) throws IndexObjectException;
	public void bulkDtoCreate(List<IndexObject> indexObjectDto) throws IndexObjectException;
	
	public void delete(Object entity) throws IndexObjectException;
	public void delete(IndexObject indexObjectDto) throws IndexObjectException;
	
	public void bulkDelete(List<?> entities) throws IndexObjectException;
	public void bulkDtoDelete(List<IndexObject> indexObjectDtos) throws IndexObjectException;
	
	public void update(Object entities) throws IndexObjectException;	
	public void update(IndexObject indexObjectDtos) throws IndexObjectException;
	
	public void bulkUpdate(List<?> entities) throws IndexObjectException;
	public void bulkDtoUpdate(List<IndexObject> indexObjectDtos) throws IndexObjectException;
	
	public void createOrUpdate(Object entity) throws IndexObjectException;
	public void createOrUpdate(IndexObject indexObjectDto) throws IndexObjectException;
	
	public void bulkCreateOrUpdate(List<?> entity) throws IndexObjectException;
	public void bulkDtoCreateOrUpdate(List<IndexObject> indexObjectDto) throws IndexObjectException;
}
