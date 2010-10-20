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
package com.jklas.search.index;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jklas.search.annotations.Indexable;
import com.jklas.search.annotations.SearchField;
import com.jklas.search.annotations.SearchId;
import com.jklas.search.index.memory.MemoryIndex;

public class MemoryIndexTest {

	@SuppressWarnings("unused")
	@Indexable
	private class Customer{
		@SearchId
		private int id = 1;
		
		@SearchField(accessByGet=true)
		private String name = "JULI";
		
		public String getName() {
			return name;
		}
	}
	
	@Before
	public void renewDefaultIndex() {
		MemoryIndex.newDefaultIndex();
	}
	
	@Test
	public void testIndexIsRenewedWhenNewIsCalled() {		
		MemoryIndex memoryIndex = MemoryIndex.getDefaultIndex();
		
		Term term = new Term("JULI");
		
		ObjectKey key = new ObjectKey(Customer.class,new Integer(0));
		memoryIndex.addToIndex(term, key,PostingMetadata.NULL);
		
		Assert.assertTrue(memoryIndex.getTermDictionarySize()==1);				
				
		memoryIndex = MemoryIndex.newDefaultIndex();
		
		Assert.assertTrue(memoryIndex.getTermDictionarySize()==0);
	}
	
	@Test
	public void testAddedPostingIsOnIndex() {		
		MemoryIndex memoryIndex = MemoryIndex.getDefaultIndex();
		
		Term term = new Term("JULI");
		
		ObjectKey key = new ObjectKey(Customer.class,new Integer(0));
		memoryIndex.addToIndex(term, key,PostingMetadata.NULL);
		
		Assert.assertTrue(memoryIndex.contains(term));		
		
		Assert.assertTrue(memoryIndex.getPostingList(term).contains(key));
	}
	
	@Test
	public void testDeletedPostingIsNotOnIndex() {		
		MemoryIndex memoryIndex = MemoryIndex.getDefaultIndex();
		
		Term term = new Term("JULI");
		ObjectKey key = new ObjectKey(Customer.class,new Integer(0));
		memoryIndex.addToIndex(term, key,PostingMetadata.NULL);
		
		Assert.assertTrue(memoryIndex.contains(term));
		
		memoryIndex.removeFromInvertedIndex(term);
		
		Assert.assertFalse(memoryIndex.contains(term));
	}

	@Test
	public void testPostingListContainsAllPostings() {		
		MemoryIndex memoryIndex = MemoryIndex.getDefaultIndex();
		Set<ObjectKey> auxSet = new HashSet<ObjectKey>();
		
		Term term = new Term("JULI");

		for (int i = 0; i < 3; i++) {			
			PostingMetadata pm = PostingMetadata.NULL;
			ObjectKey oik = new ObjectKey(Customer.class, new Integer(i));
			memoryIndex.addToIndex(term, oik, pm);
			ObjectKey posting = new ObjectKey(oik);
			auxSet.add(posting);
		}
				
		for (Entry<ObjectKey, PostingMetadata> postingEntry : memoryIndex.getPostingList(new Term("JULI"))) {
			Assert.assertTrue(auxSet.contains(postingEntry.getKey()));					
		}
	}

	
	@Test
	public void testTfEqualsNineWhen3PostingsWith3ocurrencesAreAddedToIndex() throws SecurityException, NoSuchFieldException {		
		MemoryIndex memoryIndex = MemoryIndex.getDefaultIndex();
				
		Term term = new Term("JULI");

		Field nameField = Customer.class.getDeclaredField("name");
		
		Map<Field,Serializable> fieldValueMap = new HashMap<Field,Serializable>();
		Map<Field,Integer> fieldTfMap = new HashMap<Field,Integer>();
			
		fieldTfMap.put(nameField, 3);
		
		for (int i = 0; i < 3; i++) {
			PostingMetadata pm = new PostingMetadata(fieldValueMap, fieldTfMap);
			
			ObjectKey oik = new ObjectKey(Customer.class, new Integer(i));
			
			memoryIndex.addToIndex(term, oik, pm);	
		}
		
		
		Assert.assertEquals(9,memoryIndex.getPostingList(term).getTermCount());		
	}
	
	@Test
	public void testPostingListIteratorDecrementsTf() throws SecurityException, NoSuchFieldException {	
		PostingList postingList = new PostingList(new Term("DUMMY"));
		
		Map<Field,Serializable> fieldValueMap = new HashMap<Field,Serializable>();
		Map<Field,Integer> fieldTfMap = new HashMap<Field,Integer>();
		
		Field nameField = Customer.class.getDeclaredField("name");

		fieldTfMap.put(nameField, 3);
		
		for (int i = 0; i < 3; i++) {
			PostingMetadata pm = new PostingMetadata(fieldValueMap, fieldTfMap);
			ObjectKey oik = new ObjectKey(Customer.class, new Integer(i));
			postingList.add(oik,pm);
		}
		
		Iterator<Entry<ObjectKey, PostingMetadata>> iterator = postingList.iterator();
		Assert.assertEquals(9,postingList.getTermCount());
		iterator.next();
		iterator.remove();
		Assert.assertEquals(6,postingList.getTermCount());
		iterator.next();
		iterator.remove();
		Assert.assertEquals(3,postingList.getTermCount());
		iterator.next();
		iterator.remove();
		Assert.assertEquals(0,postingList.getTermCount());
	}
	
	@Test
	public void testAddWhileIteratingDoesNotMakeNewElementsVisible() {		
		PostingList postingList = new PostingList(new Term("DUMMY"));
		Iterator<Entry<ObjectKey, PostingMetadata>> iterator = postingList.iterator();
	
		for (int i = 0; i < 3; i++) {			
			PostingMetadata pm = new PostingMetadata(null,null);
			ObjectKey oik = new ObjectKey(Customer.class, new Integer(i));
			postingList.add(oik,pm);
			
			Assert.assertFalse(iterator.hasNext());
			Assert.assertTrue(postingList.iterator().hasNext());
		}		
	}
	
	@Test
	public void testDictionarySizeIsUpdatedOk() {
		MemoryIndex memoryIndex = MemoryIndex.getDefaultIndex();
		
		ObjectKey key = new ObjectKey(Customer.class,new Integer(0));
				
		Assert.assertEquals(0,memoryIndex.getTermDictionarySize());
		memoryIndex.addToIndex(new Term("A"), key, PostingMetadata.NULL);
		Assert.assertEquals(1,memoryIndex.getTermDictionarySize());
		memoryIndex.addToIndex(new Term("B"), key, PostingMetadata.NULL);
		Assert.assertEquals(2,memoryIndex.getTermDictionarySize());
		memoryIndex.addToIndex(new Term("C"), key, PostingMetadata.NULL);
		Assert.assertEquals(3,memoryIndex.getTermDictionarySize());
		memoryIndex.removeFromInvertedIndex(new Term("A"));
		Assert.assertEquals(2,memoryIndex.getTermDictionarySize());
		memoryIndex.removeFromInvertedIndex(new Term("B"));
		Assert.assertEquals(1,memoryIndex.getTermDictionarySize());
		memoryIndex.removeFromInvertedIndex(new Term("C"));		
		Assert.assertEquals(0,memoryIndex.getTermDictionarySize());
	}
	
	@Test
	public void testDictionaryHavesAllAddedKeys() {
		MemoryIndex memoryIndex = MemoryIndex.getDefaultIndex();
		HashSet<Term> auxSet = new HashSet<Term>();
		HashSet<ObjectKey> auxKeySet = new HashSet<ObjectKey>();
		
		ObjectKey key = new ObjectKey(Customer.class,new Integer(0));
		
		auxKeySet.add(key);
		
		Term term = new Term("A");
		memoryIndex.addToIndex(term, key, PostingMetadata.NULL);
		auxSet.add(term);
		
		term = new Term("B");
		memoryIndex.addToIndex(term, key, PostingMetadata.NULL);
		auxSet.add(term);
		
		term = new Term("C");
		memoryIndex.addToIndex(term, key, PostingMetadata.NULL);
		auxSet.add(term);		
		
		for (Iterator<Term> iterator = memoryIndex.getTermDictionaryIterator(); iterator.hasNext();) {
			Assert.assertTrue(auxSet.contains(iterator.next()));			
		}
		
		for (Iterator<Entry<ObjectKey,MasterRegistryEntry>> iterator = memoryIndex.getMasterRegistryReadIterator(); iterator.hasNext();) {
			Entry<ObjectKey,MasterRegistryEntry> entry= (Entry<ObjectKey,MasterRegistryEntry>) iterator.next();
			Assert.assertTrue(auxKeySet.contains(entry.getKey()));
		}
	}
	
	@Test
	public void testDictionaryAndMasterRegistryDeletesKeys() {
		MemoryIndex memoryIndex = MemoryIndex.getDefaultIndex();
		ObjectKey key = new ObjectKey(Customer.class,new Integer(0));
	
		Term term = new Term("A");
		memoryIndex.addToIndex(term, key, PostingMetadata.NULL);
		
		term = new Term("B");
		memoryIndex.addToIndex(term, key, PostingMetadata.NULL);
		
		term = new Term("C");
		memoryIndex.addToIndex(term, key, PostingMetadata.NULL);
		
		memoryIndex.removeFromInvertedIndex(new Term("A"));
		memoryIndex.removeFromInvertedIndex(new Term("B"));
		memoryIndex.removeFromInvertedIndex(new Term("C"));
		
		Assert.assertTrue(memoryIndex.getTermDictionarySize()==0);
		
	}
	
	@Test
	public void testDeleteLastPostingDeletesPostingList() {
		MemoryIndex memoryIndex = MemoryIndex.getDefaultIndex();
		
		ObjectKey key = new ObjectKey(Customer.class,new Integer(0));
		
		Term term = new Term("A");
		
		memoryIndex.addToIndex(term, key, PostingMetadata.NULL);
		memoryIndex.removePosting(term, key);
		
		Assert.assertNull(memoryIndex.getPostingList(term));
	}
	
	
	// TODO test para verificar la sincronizacion del indice master y el invertido
}
