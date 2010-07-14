package com.jklas.search.index;

import org.junit.Assert;
import org.junit.Test;

import com.jklas.search.annotations.Indexable;
import com.jklas.search.annotations.SearchField;
import com.jklas.search.annotations.SearchId;

public class PostingListTest {

	@SuppressWarnings("unused")
	@Indexable
	private class Dummy {
		@SearchId
		private int id = 1;
		
		@SearchField(accessByGet=true)
		private String name;
		
		public Dummy(int id, String name) {
			this.id = id; this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public int getId() {
			return id;
		}
		
	}
	
	@Test
	public void testInsertSameKeyTwiceEndsUpWithOnePosting() {
		PostingList postingList = new PostingList(new Term("DUMMY"));		
		
		Dummy firstDummy = new Dummy(1,"A");
		ObjectKey oik = new ObjectKey(firstDummy.getClass(),firstDummy.getId());
		ObjectKey firstPosting = new ObjectKey(oik);
		postingList.add(oik, PostingMetadata.NULL );
		
		Assert.assertTrue(postingList.contains(oik));
		
		Dummy secondDummy = new Dummy(1,"B");
		oik = new ObjectKey(secondDummy.getClass(),secondDummy.getId());
		ObjectKey secondPosting = new ObjectKey(oik);		
		Assert.assertTrue(firstPosting.equals(secondPosting));
		
		postingList.add(oik, PostingMetadata.NULL);		
		Assert.assertTrue(postingList.contains(oik));
		Assert.assertEquals(new Integer(1), postingList.size());		
	}
	
	
	
}
