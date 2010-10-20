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
package com.jklas.search.engine.score;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.jklas.search.annotations.Indexable;
import com.jklas.search.annotations.SearchField;
import com.jklas.search.annotations.SearchId;
import com.jklas.search.annotations.SearchSort;
import com.jklas.search.engine.BooleanSearch;
import com.jklas.search.engine.Search;
import com.jklas.search.engine.VectorSearch;
import com.jklas.search.engine.dto.ObjectResult;
import com.jklas.search.index.memory.MemoryIndexReader;
import com.jklas.search.index.memory.MemoryIndexReaderFactory;
import com.jklas.search.query.bool.BooleanQueryParser;
import com.jklas.search.query.vectorial.VectorQueryParser;
import com.jklas.search.sort.PreSort;
import com.jklas.search.util.Utils;

public class HardAndSoftRulesTest {

	@Indexable	
	public class HardAndSoftRuleEntity {
		@SearchId public final int id;
		
		@SearchSort public final int proxy1;
		
		@SearchSort public final float proxy2;
		
		@SearchSort public final String proxy3;
		
		@SearchField public final String attribute;
		
		public HardAndSoftRuleEntity(int id, String attribute, int proxy1, float proxy2, String proxy3) {
			this.id = id;
			this.attribute = attribute;
			this.proxy1 = proxy1;
			this.proxy2 = proxy2;
			this.proxy3 = proxy3;
		}
	}
	
	private class HardAndSoftRule implements PreSort {

		private class ValueHolder implements Comparable<ValueHolder> {
			public ObjectResult okr;
			public float score;

			public ValueHolder(ObjectResult okr, float score) {this.okr = okr; this.score =score;}

			@Override
			public int compareTo(ValueHolder o) {return Float.compare(score, o.score);}
		}
				
		private final Field proxy1Field, proxy2Field ;
				
		public HardAndSoftRule() throws SecurityException, NoSuchFieldException {			
			this.proxy1Field = HardAndSoftRuleEntity.class.getDeclaredField("proxy1");
			this.proxy2Field = HardAndSoftRuleEntity.class.getDeclaredField("proxy2");
		}
		
		protected final boolean objectAccepted(ObjectResult object) {
			return HardAndSoftRuleEntity.class.equals(object.getKey().getClazz());
		}
		
		@Override
		public List<? extends ObjectResult> work(Collection<? extends ObjectResult> currentObjects) {
			if(currentObjects==null) throw new IllegalArgumentException("Can't work on a null result set");
			
			List<ValueHolder> treated = new ArrayList<ValueHolder>();
			
			int proxy1Max = 0;
			
			for (Iterator<? extends ObjectResult> iterator = currentObjects.iterator(); iterator.hasNext();) {
				ObjectResult okr = (ObjectResult) iterator.next();
				
				if(!objectAccepted(okr)) continue;
				
				iterator.remove();
				
				treated.add(new ValueHolder(okr, 0f));
				
				int proxy1Value = (Integer)okr.getStoredFields().get(proxy1Field);
				
				if(proxy1Value>proxy1Max) proxy1Max = proxy1Value;
			}
			
			for (ValueHolder valueHolder : treated) {
				float proxy2Value = (Float)valueHolder.okr.getStoredFields().get(proxy2Field) / (float) proxy1Max;
				valueHolder.score = proxy2Value;
			}

			Collections.sort(treated);
			List<ObjectResult> result = new ArrayList<ObjectResult>(treated.size() + currentObjects.size());

			for (ValueHolder valueHolder : treated) {
				result.add(valueHolder.okr);
			}
			
			result.addAll(currentObjects);
			
			return result;
		}
	}
	
	@Test
	public void BooleanRetrievalMaxRule() throws SecurityException, NoSuchFieldException {
		HardAndSoftRuleEntity entity1 = new HardAndSoftRuleEntity(0,"Something to be retrieved", 1 , 50.0f , "A");
		HardAndSoftRuleEntity entity2 = new HardAndSoftRuleEntity(1,"Another thing to be retrieved", 5 , 20.0f , "A");
		HardAndSoftRuleEntity entity3 = new HardAndSoftRuleEntity(2,"I should be retrieved too!", 10 , 10.0f , "A");
	
		Utils.setupSampleMemoryIndex(entity1, entity2, entity3);
		
		BooleanSearch search = new BooleanSearch( new BooleanQueryParser("retrieved").getQuery(), MemoryIndexReaderFactory.getInstance() );
				
		List<? extends ObjectResult> results = search.search(new HardAndSoftRule());
		
		Assert.assertEquals(2, results.get(0).getKey().getId());
		Assert.assertEquals(1, results.get(1).getKey().getId());
		Assert.assertEquals(0, results.get(2).getKey().getId());		
	}
	
	@Test
	public void VectorRetrievalMaxRule() throws SecurityException, NoSuchFieldException {
		HardAndSoftRuleEntity entity1 = new HardAndSoftRuleEntity(0,"Something to be retrieved", 1 , 50.0f , "A");
		HardAndSoftRuleEntity entity2 = new HardAndSoftRuleEntity(1,"Another thing to be retrieved", 5 , 20.0f , "A");
		HardAndSoftRuleEntity entity3 = new HardAndSoftRuleEntity(2,"I should be retrieved too!", 10 , 10.0f , "A");
	
		Utils.setupSampleMemoryIndex(entity1, entity2, entity3);
		
		Search search = new VectorSearch( new VectorQueryParser("retrieved").getQuery(), new MemoryIndexReader() );
				
		List<? extends ObjectResult> results = (List<? extends ObjectResult>) search.search(new HardAndSoftRule());
		
		Assert.assertEquals(2, results.get(0).getKey().getId());
		Assert.assertEquals(1, results.get(1).getKey().getId());
		Assert.assertEquals(0, results.get(2).getKey().getId());		
	}

	private class HardAndSoftRuleInverse implements PreSort {

		private class ValueHolder implements Comparable<ValueHolder> {
			public ObjectResult okr;
			public float score;

			public ValueHolder(ObjectResult okr, float score) {this.okr = okr; this.score =score;}

			@Override
			public int compareTo(ValueHolder o) {return Float.compare(score, o.score);}
		}
				
		private final Field proxy1Field, proxy2Field ;
				
		public HardAndSoftRuleInverse() throws SecurityException, NoSuchFieldException {			
			this.proxy1Field = HardAndSoftRuleEntity.class.getDeclaredField("proxy1");
			this.proxy2Field = HardAndSoftRuleEntity.class.getDeclaredField("proxy2");
		}
		
		protected final boolean objectAccepted(ObjectResult object) {
			return HardAndSoftRuleEntity.class.equals(object.getKey().getClazz());
		}
		
		@Override
		public List<? extends ObjectResult> work(Collection<? extends ObjectResult> currentObjects) {
			if(currentObjects==null) throw new IllegalArgumentException("Can't work on a null result set");
			
			List<ValueHolder> treated = new ArrayList<ValueHolder>();
			
			int proxy1Max = 0;
			
			for (Iterator<? extends ObjectResult> iterator = currentObjects.iterator(); iterator.hasNext();) {
				ObjectResult okr = (ObjectResult) iterator.next();
				
				if(!objectAccepted(okr)) continue;
				
				iterator.remove();
				
				treated.add(new ValueHolder(okr, 0f));
				
				int proxy1Value = (Integer)okr.getStoredFields().get(proxy1Field);
				
				if(proxy1Value>proxy1Max) proxy1Max = proxy1Value;
			}
			
			for (ValueHolder valueHolder : treated) {
				float proxy2Value = (Float)valueHolder.okr.getStoredFields().get(proxy2Field) / (float) proxy1Max;
				valueHolder.score = proxy2Value;
			}

			Collections.sort(treated);
			List<ObjectResult> result = new ArrayList<ObjectResult>(treated.size() + currentObjects.size());

			for (ValueHolder valueHolder : treated) {
				result.add(valueHolder.okr);
			}
			
			result.addAll(currentObjects);
			
			Collections.reverse(result);
			
			return result;
		}
	}
	
	@Test
	public void BooleanRetrievalMaxRuleInverted() throws SecurityException, NoSuchFieldException {
		HardAndSoftRuleEntity entity1 = new HardAndSoftRuleEntity(0,"Something to be retrieved", 1 , 50.0f , "A");
		HardAndSoftRuleEntity entity2 = new HardAndSoftRuleEntity(1,"Another thing to be retrieved", 5 , 20.0f , "A");
		HardAndSoftRuleEntity entity3 = new HardAndSoftRuleEntity(2,"I should be retrieved too!", 10 , 10.0f , "A");
	
		Utils.setupSampleMemoryIndex(entity1, entity2, entity3);
		
		BooleanSearch search = new BooleanSearch( new BooleanQueryParser("retrieved").getQuery(), MemoryIndexReaderFactory.getInstance() );
				
		List<? extends ObjectResult> results = search.search(new HardAndSoftRuleInverse());
		
		Assert.assertEquals(0, results.get(0).getKey().getId());
		Assert.assertEquals(1, results.get(1).getKey().getId());
		Assert.assertEquals(2, results.get(2).getKey().getId());		
	}
	
	@Test
	public void VectorRetrievalMaxRuleInverted() throws SecurityException, NoSuchFieldException {
		HardAndSoftRuleEntity entity1 = new HardAndSoftRuleEntity(0,"Something to be retrieved", 1 , 50.0f , "A");
		HardAndSoftRuleEntity entity2 = new HardAndSoftRuleEntity(1,"Another thing to be retrieved", 5 , 20.0f , "A");
		HardAndSoftRuleEntity entity3 = new HardAndSoftRuleEntity(2,"I should be retrieved too!", 10 , 10.0f , "A");
	
		Utils.setupSampleMemoryIndex(entity1, entity2, entity3);
		
		Search search = new VectorSearch( new VectorQueryParser("retrieved").getQuery(), new MemoryIndexReader() );
				
		List<? extends ObjectResult> results = (List<? extends ObjectResult>) search.search(new HardAndSoftRuleInverse());
		
		Assert.assertEquals(0, results.get(0).getKey().getId());
		Assert.assertEquals(1, results.get(1).getKey().getId());
		Assert.assertEquals(2, results.get(2).getKey().getId());		
	}
}
