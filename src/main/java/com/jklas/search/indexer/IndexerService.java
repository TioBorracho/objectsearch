package com.jklas.search.indexer;

import java.util.List;

import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.index.dto.IndexObjectDto;

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
	public void create(IndexObjectDto indexObjectDto) throws IndexObjectException;	
	
	public void bulkCreate(List<?> entity) throws IndexObjectException;
	public void bulkDtoCreate(List<IndexObjectDto> indexObjectDto) throws IndexObjectException;
	
	public void delete(Object entity) throws IndexObjectException;
	public void delete(IndexObjectDto indexObjectDto) throws IndexObjectException;
	
	public void bulkDelete(List<?> entities) throws IndexObjectException;
	public void bulkDtoDelete(List<IndexObjectDto> indexObjectDtos) throws IndexObjectException;
	
	public void update(Object entities) throws IndexObjectException;	
	public void update(IndexObjectDto indexObjectDtos) throws IndexObjectException;
	
	public void bulkUpdate(List<?> entities) throws IndexObjectException;
	public void bulkDtoUpdate(List<IndexObjectDto> indexObjectDtos) throws IndexObjectException;
	
	public void createOrUpdate(Object entity) throws IndexObjectException;
	public void createOrUpdate(IndexObjectDto indexObjectDto) throws IndexObjectException;
	
	public void bulkCreateOrUpdate(List<?> entity) throws IndexObjectException;
	public void bulkDtoCreateOrUpdate(List<IndexObjectDto> indexObjectDto) throws IndexObjectException;
}
