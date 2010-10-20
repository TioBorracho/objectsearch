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
package com.jklas.search.engine.filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jklas.search.engine.dto.ObjectResult;

public class DateRangeFilter implements ResultFilter {

	private final long startTimestamp, endTimestamp;
	
	private final List<Field> fieldsToFilterBy = new ArrayList<Field>();
	
	private DateRangeFilter(Date start, Date end) {
		this.startTimestamp = start.getTime();
		this.endTimestamp = end.getTime();
		
		if(endTimestamp<startTimestamp) throw new IllegalArgumentException("End date must be after start date");
	}
	
	public DateRangeFilter(Date start, Date end, Field... filterFields) {
		this(start,end);

		if(filterFields == null || (filterFields!=null && filterFields.length == 0)) 
			throw new IllegalArgumentException("The field list can't be either null or empty");
		
		for (Field field : filterFields) {	
			if(field==null)
				throw new IllegalArgumentException("The field to use for filtering can't be null");
			
			if(!field.getType().isAssignableFrom(Date.class))
				throw new IllegalArgumentException("The field to use for filtering must be a Date or a subclass of it");
			
			fieldsToFilterBy.add(field);
		}
		
	}

	public boolean isFiltered(ObjectResult filtrable) {
		Map<Field, Object> storedFields = filtrable.getStoredFields();
		
		for (Field filterField : fieldsToFilterBy) {
			if(!storedFields.containsKey(filterField)) continue;
			
			Date filtrableDate = (Date)storedFields.get(filterField);
			
			long filtrableTimestamp = filtrableDate.getTime();
			
			if(startTimestamp > filtrableTimestamp || endTimestamp < filtrableTimestamp) return true;
			else continue;			
		}
				
		return false;
	}
	
}
