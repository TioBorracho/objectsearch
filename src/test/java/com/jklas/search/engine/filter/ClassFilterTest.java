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

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.jklas.search.annotations.Indexable;
import com.jklas.search.annotations.SearchField;
import com.jklas.search.annotations.SearchId;
import com.jklas.search.engine.BooleanSearch;
import com.jklas.search.engine.dto.ObjectKeyResult;
import com.jklas.search.index.memory.MemoryIndexReaderFactory;
import com.jklas.search.query.bool.BooleanQuery;
import com.jklas.search.query.bool.BooleanQueryParser;
import com.jklas.search.util.Utils;

public class ClassFilterTest {
	
	// TODO re-anotar la jerarquía cuando la indexación
	// jerárquica funcione ok
	
	@Indexable
	@SuppressWarnings("unused")
	private class Entity {
		@SearchField public String attribute = "something";
		
		@SearchId public final int id;
		public Entity(int id) {	this.id = id; }
	}
	
	@Indexable
	@SuppressWarnings("unused")
	private class EntityA extends Entity {
		
		@SearchField public String attribute = "something";
		
		@SearchId public final int otherId;
		
		public EntityA(int id) { super(id); this.otherId = id; }
	}
	
	@Indexable
	@SuppressWarnings("unused")
	private class EntityB extends Entity {
		
		@SearchField public String attribute = "something";
		
		@SearchId public final int otherId;
	
		public EntityB(int id) { super(id); this.otherId = id; }
	}
	
	@Test
	public void SpecifiedClassIsNotFilteredWhenNotAllowingSubclasses() throws SecurityException, NoSuchFieldException {

		Entity entity = new Entity(0);
		
		Utils.setupSampleMemoryIndex(entity);

		BooleanQueryParser parser = new BooleanQueryParser("something");
		BooleanQuery query = parser.getQuery();
		BooleanSearch booleanSearch = new BooleanSearch(query, MemoryIndexReaderFactory.getInstance());		
		Set<ObjectKeyResult> results = booleanSearch.search();

		Assert.assertEquals(1, results.size() );

		ClassFilter filter = new ClassFilter(entity.getClass(),false);
		
		Assert.assertFalse(filter.isFiltered(results.iterator().next()));
	}

	@Test
	public void SpecifiedClassIsNotFilteredWhenAllowingSubclasses() throws SecurityException, NoSuchFieldException {

		Entity entity = new Entity(0);
		
		Utils.setupSampleMemoryIndex(entity);

		BooleanQueryParser parser = new BooleanQueryParser("something");
		BooleanQuery query = parser.getQuery();
		BooleanSearch booleanSearch = new BooleanSearch(query, MemoryIndexReaderFactory.getInstance());		
		Set<ObjectKeyResult> results = booleanSearch.search();

		Assert.assertEquals(1, results.size() );

		ClassFilter filter = new ClassFilter(entity.getClass(),true);
		
		Assert.assertFalse(filter.isFiltered(results.iterator().next()));
	}

	
	@Test
	public void UnspecifiedClassIsFilteredWhenNotAllowingSubclasses() throws SecurityException, NoSuchFieldException {

		Entity entity = new Entity(0);
		
		Utils.setupSampleMemoryIndex(entity);

		BooleanQueryParser parser = new BooleanQueryParser("something");
		BooleanQuery query = parser.getQuery();
		BooleanSearch booleanSearch = new BooleanSearch(query, MemoryIndexReaderFactory.getInstance());		
		Set<ObjectKeyResult> results = booleanSearch.search();

		Assert.assertEquals(1, results.size() );

		ClassFilter filter = new ClassFilter(Integer.class,true);
		
		Assert.assertTrue(filter.isFiltered(results.iterator().next()));
	}
	
	@Test
	public void UnspecifiedClassIsFilteredWhenAllowingSubclasses() throws SecurityException, NoSuchFieldException {

		Entity entity = new Entity(0);
		
		Utils.setupSampleMemoryIndex(entity);

		BooleanQueryParser parser = new BooleanQueryParser("something");
		BooleanQuery query = parser.getQuery();
		BooleanSearch booleanSearch = new BooleanSearch(query, MemoryIndexReaderFactory.getInstance());		
		Set<ObjectKeyResult> results = booleanSearch.search();

		Assert.assertEquals(1, results.size() );

		ClassFilter filter = new ClassFilter(Integer.class,true);
		
		Assert.assertTrue(filter.isFiltered(results.iterator().next()));
	}
	
	@Test
	public void SpecifiedClassAndSubclassesAreNotFilteredWhenAllowingSubclasses() throws SecurityException, NoSuchFieldException {

		Entity entity = new Entity(0);
		EntityA entityA = new EntityA(1);
		EntityB entityB = new EntityB(2);
		
		Utils.setupSampleMemoryIndex(entity,entityA,entityB);

		BooleanQueryParser parser = new BooleanQueryParser("something");
		BooleanQuery query = parser.getQuery();
		BooleanSearch booleanSearch = new BooleanSearch(query, MemoryIndexReaderFactory.getInstance());		
		Set<ObjectKeyResult> results = booleanSearch.search();

		Assert.assertEquals(3, results.size() );

		ClassFilter filter = new ClassFilter(Entity.class,true);		
		
		Assert.assertFalse(filter.isFiltered(results.iterator().next()));
		Assert.assertFalse(filter.isFiltered(results.iterator().next()));
		Assert.assertFalse(filter.isFiltered(results.iterator().next()));
	}

	@Test
	public void SpecifiedClassAndSubclassesAreFilteredWhenNotAllowingSubclasses() throws SecurityException, NoSuchFieldException {

		Entity entity = new Entity(0);
		EntityA entityA = new EntityA(1);
		EntityB entityB = new EntityB(2);
		
		Utils.setupSampleMemoryIndex(entity,entityA,entityB);

		BooleanQueryParser parser = new BooleanQueryParser("something");
		BooleanQuery query = parser.getQuery();
		BooleanSearch booleanSearch = new BooleanSearch(query, MemoryIndexReaderFactory.getInstance());		
		Set<ObjectKeyResult> results = booleanSearch.search();

		Assert.assertEquals(3, results.size() );

		ClassFilter filter = new ClassFilter(Entity.class,false);		

		for (ObjectKeyResult indexObjectResult : results) {
			if(Entity.class.equals(indexObjectResult.getKey().getClazz())) {
				Assert.assertFalse(filter.isFiltered(indexObjectResult));				
			} else {
				Assert.assertTrue(filter.isFiltered(indexObjectResult));				
			}
		}		
	}	
}
