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
