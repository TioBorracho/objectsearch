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

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.jklas.search.annotations.Indexable;
import com.jklas.search.annotations.SearchField;
import com.jklas.search.annotations.SearchFilter;
import com.jklas.search.annotations.SearchId;
import com.jklas.search.engine.BooleanSearch;
import com.jklas.search.engine.VectorSearch;
import com.jklas.search.engine.dto.ObjectKeyResult;
import com.jklas.search.engine.dto.VectorRankedResult;
import com.jklas.search.index.memory.MemoryIndexReader;
import com.jklas.search.index.memory.MemoryIndexReaderFactory;
import com.jklas.search.query.bool.BooleanQuery;
import com.jklas.search.query.bool.BooleanQueryParser;
import com.jklas.search.query.vectorial.VectorQuery;
import com.jklas.search.query.vectorial.VectorQueryParser;
import com.jklas.search.util.Utils;

public class FilterTest {

	@SuppressWarnings("unused")
	@Indexable
	private class EntityWithGetAccess1983 {
		@SearchId public int id;

		@SearchField public String attribute;

		@SearchFilter(accessByGet=true) public Date dateOfBirth;

		public Date getDateOfBirth() {
			Calendar birthCalendar = Calendar.getInstance();	
			birthCalendar.set(Calendar.YEAR,1983);
			birthCalendar.set(Calendar.MONTH,03);
			birthCalendar.set(Calendar.DAY_OF_MONTH,30);
			birthCalendar.set(Calendar.MINUTE,0);
			birthCalendar.set(Calendar.SECOND,0);
			birthCalendar.set(Calendar.MILLISECOND,0);
			return birthCalendar.getTime();
		}
	}
	
	@SuppressWarnings("unused")
	@Indexable
	private class EntityWithGetAccess2983 {
		@SearchId public int id;

		@SearchField public String attribute;

		@SearchFilter(accessByGet=true) public Date dateOfBirth;

		public Date getDateOfBirth() {
			Calendar birthCalendar = Calendar.getInstance();	
			birthCalendar.set(Calendar.YEAR,2983);
			birthCalendar.set(Calendar.MONTH,03);
			birthCalendar.set(Calendar.DAY_OF_MONTH,1);
			birthCalendar.set(Calendar.MINUTE,0);
			birthCalendar.set(Calendar.SECOND,0);
			birthCalendar.set(Calendar.MILLISECOND,0);
			return birthCalendar.getTime();
		}
	}

	@Test
	public void FilterUsesAccessByGetOnBooleanSearch() throws SecurityException, NoSuchFieldException {
		Date fakeDate = new Date();

		EntityWithGetAccess1983 dummy1983   = new EntityWithGetAccess1983();
		dummy1983.id = 0;
		dummy1983.attribute = "julian";
		dummy1983.dateOfBirth = fakeDate;

		EntityWithGetAccess2983 dummy2983   = new EntityWithGetAccess2983();
		dummy2983.id = 1;
		dummy2983.attribute = "karl";
		dummy2983.dateOfBirth = fakeDate;

		Utils.setupSampleMemoryIndex(dummy1983,dummy2983);

		BooleanQueryParser parser = new BooleanQueryParser("julian +OR karl");
		BooleanQuery query = parser.getQuery();
		BooleanSearch booleanSearch = new BooleanSearch(query, MemoryIndexReaderFactory.getInstance());		
		Set<ObjectKeyResult> results = booleanSearch.search();

		Assert.assertEquals(2, results.size() );

		Calendar start = Calendar.getInstance(), end = Calendar.getInstance();
		start.set(Calendar.YEAR,1983);start.set(Calendar.MONTH,03);start.set(Calendar.DAY_OF_MONTH,30);
		start.set(Calendar.MINUTE,0);start.set(Calendar.SECOND,0);start.set(Calendar.MILLISECOND,0);

		end.set(Calendar.YEAR,1990);end.set(Calendar.MONTH,01);end.set(Calendar.DAY_OF_MONTH,1);
		end.set(Calendar.MINUTE,0);	end.set(Calendar.SECOND,0);	end.set(Calendar.MILLISECOND,0);


		for (Iterator<ObjectKeyResult> iterator = results.iterator(); iterator.hasNext();) {
			ObjectKeyResult currentResult = (ObjectKeyResult) iterator.next();

			DateRangeFilter dateOfBirthFilter = new DateRangeFilter(start.getTime(), end.getTime(), currentResult.getKey().getClazz().getDeclaredField("dateOfBirth"));

			if(dateOfBirthFilter.isFiltered(currentResult)) iterator.remove();
		}

		Assert.assertEquals(1, results.size() );
	}

	@Test
	public void FilterUsesAccessByGetOnVectorSearch() throws SecurityException, NoSuchFieldException {

		Date fakeDate = new Date();

		EntityWithGetAccess1983 dummy1983   = new EntityWithGetAccess1983();
		dummy1983.id = 0;
		dummy1983.attribute = "julian";
		dummy1983.dateOfBirth = fakeDate;

		EntityWithGetAccess2983 dummy2983   = new EntityWithGetAccess2983();
		dummy2983.id = 1;
		dummy2983.attribute = "karl";
		dummy2983.dateOfBirth = fakeDate;

		Utils.setupSampleMemoryIndex(dummy1983,dummy2983);

		VectorQueryParser parser = new VectorQueryParser("julian karl");
		VectorQuery query = parser.getQuery();
		List<VectorRankedResult> results = new VectorSearch(query, new MemoryIndexReader()).search();

		Assert.assertEquals(2, results.size() );

		Calendar start = Calendar.getInstance(), end = Calendar.getInstance();
		start.set(Calendar.YEAR,1983);start.set(Calendar.MONTH,03);start.set(Calendar.DAY_OF_MONTH,30);
		start.set(Calendar.MINUTE,0);start.set(Calendar.SECOND,0);start.set(Calendar.MILLISECOND,0);

		end.set(Calendar.YEAR,1990);end.set(Calendar.MONTH,01);end.set(Calendar.DAY_OF_MONTH,1);
		end.set(Calendar.MINUTE,0);	end.set(Calendar.SECOND,0);	end.set(Calendar.MILLISECOND,0);


		for (Iterator<VectorRankedResult> iterator = results.iterator(); iterator.hasNext();) {
			VectorRankedResult currentResult = (VectorRankedResult) iterator.next();

			DateRangeFilter dateOfBirthFilter = new DateRangeFilter(start.getTime(), end.getTime(), currentResult.getKey().getClazz().getDeclaredField("dateOfBirth"));

			if(dateOfBirthFilter.isFiltered(currentResult)) iterator.remove();
		}

		Assert.assertEquals(1, results.size() );
	}

	
	@Test
	public void FilterInsideBooleanSearch() throws SecurityException, NoSuchFieldException {
		Date fakeDate = new Date();

		EntityWithGetAccess1983 dummy1983   = new EntityWithGetAccess1983();
		dummy1983.id = 0;
		dummy1983.attribute = "julian";
		dummy1983.dateOfBirth = fakeDate;

		EntityWithGetAccess2983 dummy2983   = new EntityWithGetAccess2983();
		dummy2983.id = 1;
		dummy2983.attribute = "karl";
		dummy2983.dateOfBirth = fakeDate;

		Utils.setupSampleMemoryIndex(dummy1983,dummy2983);

		DateRangeFilter dateOfBirthFilter = setupDateRangeFilter(1983,3,30,1990,1,1);	
		
		BooleanQueryParser parser = new BooleanQueryParser("julian +OR karl");
		BooleanQuery query = parser.getQuery();
		BooleanSearch booleanSearch = new BooleanSearch(query, MemoryIndexReaderFactory.getInstance());
		FilterChain filterChain = new ImmediateRemoveFilterChain(dateOfBirthFilter);
		Set<ObjectKeyResult> results = booleanSearch.search(filterChain);

		Assert.assertEquals(1, results.size() );
	}

	@Test
	public void LateFilterInsideBooleanSearch() throws SecurityException, NoSuchFieldException {
		Date fakeDate = new Date();

		EntityWithGetAccess1983 dummy1983   = new EntityWithGetAccess1983();
		dummy1983.id = 0;
		dummy1983.attribute = "julian";
		dummy1983.dateOfBirth = fakeDate;

		EntityWithGetAccess2983 dummy2983   = new EntityWithGetAccess2983();
		dummy2983.id = 1;
		dummy2983.attribute = "karl";
		dummy2983.dateOfBirth = fakeDate;

		Utils.setupSampleMemoryIndex(dummy1983,dummy2983);

		DateRangeFilter dateOfBirthFilter = setupDateRangeFilter(1983,3,30,1990,1,1);	
		
		BooleanQueryParser parser = new BooleanQueryParser("julian +OR karl");
		BooleanQuery query = parser.getQuery();
		BooleanSearch booleanSearch = new BooleanSearch(query, MemoryIndexReaderFactory.getInstance());
		FilterChain filterChain = new LateRemoveFilterChain(dateOfBirthFilter);
		Set<ObjectKeyResult> results = booleanSearch.search(filterChain);

		Assert.assertEquals(1, results.size() );
	}
	
	@Test
	public void FilterInsideVectorSearch() throws SecurityException, NoSuchFieldException {

		Date fakeDate = new Date();

		EntityWithGetAccess1983 dummy1983   = new EntityWithGetAccess1983();
		dummy1983.id = 0;
		dummy1983.attribute = "julian";
		dummy1983.dateOfBirth = fakeDate;

		EntityWithGetAccess2983 dummy2983   = new EntityWithGetAccess2983();
		dummy2983.id = 1;
		dummy2983.attribute = "karl";
		dummy2983.dateOfBirth = fakeDate;

		DateRangeFilter dateOfBirthFilter = setupDateRangeFilter(1983,3,30,1990,1,1);
		
		Utils.setupSampleMemoryIndex(dummy1983,dummy2983);
		
		VectorQueryParser parser = new VectorQueryParser("julian karl");
		VectorQuery query = parser.getQuery();
		FilterChain filterChain = new ImmediateRemoveFilterChain(dateOfBirthFilter);
		List<VectorRankedResult> results = new VectorSearch(query, new MemoryIndexReader()).search(filterChain);

		Assert.assertEquals(1, results.size() );
	}

	@Test
	public void LateInsideVectorSearch() throws SecurityException, NoSuchFieldException {

		Date fakeDate = new Date();

		EntityWithGetAccess1983 dummy1983   = new EntityWithGetAccess1983();
		dummy1983.id = 0;
		dummy1983.attribute = "julian";
		dummy1983.dateOfBirth = fakeDate;

		EntityWithGetAccess2983 dummy2983   = new EntityWithGetAccess2983();
		dummy2983.id = 1;
		dummy2983.attribute = "karl";
		dummy2983.dateOfBirth = fakeDate;

		DateRangeFilter dateOfBirthFilter = setupDateRangeFilter(1983,3,30,1990,1,1);
		
		Utils.setupSampleMemoryIndex(dummy1983,dummy2983);
		
		VectorQueryParser parser = new VectorQueryParser("julian karl");
		VectorQuery query = parser.getQuery();
		FilterChain filterChain = new LateRemoveFilterChain(dateOfBirthFilter);
		List<VectorRankedResult> results = new VectorSearch(query, new MemoryIndexReader()).search(filterChain);

		Assert.assertEquals(1, results.size() );
	}
	
	private DateRangeFilter setupDateRangeFilter(int startY, int startM, int startD, int endY, int endM, int endD) throws NoSuchFieldException {
		Calendar start = Calendar.getInstance(), end = Calendar.getInstance();
		start.set(Calendar.YEAR,startY);start.set(Calendar.MONTH,startM);start.set(Calendar.DAY_OF_MONTH,startD);
		start.set(Calendar.MINUTE,0);start.set(Calendar.SECOND,0);start.set(Calendar.MILLISECOND,0);
		
		end.set(Calendar.YEAR,endY);end.set(Calendar.MONTH,endM);end.set(Calendar.DAY_OF_MONTH,endD);
		end.set(Calendar.MINUTE,0);	end.set(Calendar.SECOND,0);	end.set(Calendar.MILLISECOND,0);
		DateRangeFilter dateOfBirthFilter = new DateRangeFilter(start.getTime(), end.getTime(),
				EntityWithGetAccess2983.class.getDeclaredField("dateOfBirth"),
				EntityWithGetAccess1983.class.getDeclaredField("dateOfBirth"));
		return dateOfBirthFilter;
	}
	
}
