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
