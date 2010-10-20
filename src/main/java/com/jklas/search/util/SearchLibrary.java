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
package com.jklas.search.util;

import java.util.ArrayList;
import java.util.List;

import com.jklas.search.SearchEngine;
import com.jklas.search.configuration.AnnotationConfigurationMapper;
import com.jklas.search.exception.SearchEngineMappingException;
import com.jklas.search.index.dto.IndexObjectDto;


public class SearchLibrary {

	public static int intCompareTo(int x, int y) {		
		return (x < y ? -1 : ( x == y ? 0 : 1));
	}
	
	/**
	 * Correctly truncates up to 7 decimals
	 * @param number the number to be truncated
	 * @param decimals the number of decimals to retain
	 * @return a number that matches the number parameter up to the {decimal}'th decimal number 
	 */
	public static Double trunc(double number, int decimals) {		
		long scale = (long)Math.pow(10,decimals);
		return (double)Math.round( Math.floor(number * scale))/ scale;
	}
	
	public static List<Object> convertDtoListToEntityList(List<IndexObjectDto> indexObjectDto) {
		List<Object> entities = new ArrayList<Object>(indexObjectDto.size());

		for (IndexObjectDto dto : indexObjectDto) {
			entities.add(dto);
		}
		return entities;
	}

	public static void configureAndMap(Object entity) throws SearchEngineMappingException {
		SearchEngine search = SearchEngine.getInstance();
		
		if(!search.isConfigured()) search.newConfiguration();
		
		AnnotationConfigurationMapper acm = new AnnotationConfigurationMapper();
		
		acm.map(entity);		
	}
	
	public static void configureAndMap(Class<?>[] clazz) throws SearchEngineMappingException {
		SearchEngine search = SearchEngine.getInstance();
		
		if(!search.isConfigured()) search.newConfiguration();
		
		AnnotationConfigurationMapper acm = new AnnotationConfigurationMapper();
		
		for (Class<?> currentClazz : clazz) {
			acm.map(currentClazz);					
		}
	}
	
	public static void configureAndMap(Class<?> clazz) throws SearchEngineMappingException {
		configureAndMap(new Class<?>[]{clazz});		
	}
}
