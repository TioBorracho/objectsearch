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
import java.util.Comparator;
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
import com.jklas.search.engine.dto.ObjectKeyResult;
import com.jklas.search.engine.dto.ObjectResult;
import com.jklas.search.engine.dto.VectorRankedResult;
import com.jklas.search.index.memory.MemoryIndexReaderFactory;
import com.jklas.search.query.bool.BooleanQueryParser;
import com.jklas.search.query.vectorial.VectorQueryParser;
import com.jklas.search.sort.ReverseComparator;
import com.jklas.search.util.SearchLibrary;
import com.jklas.search.util.Utils;

public class SortTest {

	@SuppressWarnings("unused")
	@Indexable
	private class SortEntity {
		@SearchId private int id ;		
		@SearchSort	private float price;		
		@SearchField private String title;
		public SortEntity(int id, String title, float price) {this.id = id ; this.title = title; this.price = price;}
	}

	private class PriceComparator implements Comparator<ObjectResult> {
		private final Field priceField;

		public PriceComparator(Field priceField) {this.priceField = priceField;}		

		public int compare(ObjectResult o1, ObjectResult o2) {			
			if(!o1.getKey().getClazz().equals(o2.getKey().getClazz())) return 0;			
			return ((Float)o1.getStoredFields().get(priceField)).compareTo((Float)o2.getStoredFields().get(priceField));
		}
	}

	@Test
	public void BooleanSearchOneFieldSort() throws SecurityException, NoSuchFieldException {
		SortEntity ipod300usd = new SortEntity(0,"ipod touch 32 GB", 300);
		SortEntity ipod230usd = new SortEntity(1,"ipod touch 16 GB", 230);
		SortEntity ipod200usd = new SortEntity(2,"ipod touch 8 GB", 200);

		Utils.setupSampleMemoryIndex(ipod230usd, ipod200usd ,ipod300usd);

		BooleanSearch search = new BooleanSearch(new BooleanQueryParser("ipod touch").getQuery(), MemoryIndexReaderFactory.getInstance());

		Comparator<ObjectResult> priceComparator = new PriceComparator(SortEntity.class.getDeclaredField("price"));

		List<ObjectKeyResult> results = search.search( priceComparator );

		Assert.assertEquals(2, results.get(0).getKey().getId());
		Assert.assertEquals(1, results.get(1).getKey().getId());
		Assert.assertEquals(0, results.get(2).getKey().getId());		 
	}

	@Test
	public void BooleanSearchOneFieldInverseSort() throws SecurityException, NoSuchFieldException {
		SortEntity ipod300usd = new SortEntity(0,"ipod touch 32 GB", 200);
		SortEntity ipod230usd = new SortEntity(1,"ipod touch 16 GB", 230);
		SortEntity ipod200usd = new SortEntity(2,"ipod touch 8 GB", 300);

		Utils.setupSampleMemoryIndex(ipod230usd, ipod200usd ,ipod300usd);

		BooleanSearch search = new BooleanSearch(new BooleanQueryParser("ipod touch").getQuery(), MemoryIndexReaderFactory.getInstance());

		Comparator<ObjectResult> priceComparator = new PriceComparator(SortEntity.class.getDeclaredField("price"));

		List<ObjectKeyResult> results = search.search( priceComparator );

		Assert.assertEquals(0, results.get(0).getKey().getId());
		Assert.assertEquals(1, results.get(1).getKey().getId());
		Assert.assertEquals(2, results.get(2).getKey().getId());		 
	}

	@SuppressWarnings("unused")
	@Indexable
	private class TwoFieldsSortEntity {
		@SearchId private int id ;		
		@SearchSort	private float price;		
		@SearchSort	private int year;
		@SearchField private String title;
		public TwoFieldsSortEntity (int id, String title, int year, int price)
		{this.id = id ; this.title = title; this.price = price; this.year = year;}
	}

	private class YearPriceComparator implements Comparator<ObjectResult> {
		private final Field priceField, yearField;

		public YearPriceComparator(Field yearField, Field priceField)
		{ this.yearField = yearField; this.priceField = priceField;}		

		public int compare(ObjectResult o1, ObjectResult o2) {			
			if(!o1.getKey().getClazz().equals(o2.getKey().getClazz())) return 0;

			int yearCompare = SearchLibrary.intCompareTo((Integer)o1.getStoredFields().get(yearField),
					(Integer)o2.getStoredFields().get(yearField));

			if(yearCompare!=0) return yearCompare;

			return ((Float)o1.getStoredFields().get(priceField)).compareTo((Float)o2.getStoredFields().get(priceField));
		}
	}

	@Test
	public void BooleanSearchTwoFieldSort() throws SecurityException, NoSuchFieldException {
		TwoFieldsSortEntity ipod_1998_300usd = new TwoFieldsSortEntity(0,"ipod touch 32 GB",1998, 300);
		TwoFieldsSortEntity ipod_1999_230usd = new TwoFieldsSortEntity(1,"ipod touch 16 GB",1999, 230);
		TwoFieldsSortEntity ipod_2000_200usd = new TwoFieldsSortEntity(2,"ipod touch 8 GB" ,2000, 200);

		Utils.setupSampleMemoryIndex(ipod_1999_230usd, ipod_2000_200usd ,ipod_1998_300usd);

		BooleanSearch search = new BooleanSearch(new BooleanQueryParser("ipod touch").getQuery(), MemoryIndexReaderFactory.getInstance());

		Comparator<ObjectResult> yearPriceComparator =
			new YearPriceComparator(TwoFieldsSortEntity.class.getDeclaredField("year"),
					TwoFieldsSortEntity.class.getDeclaredField("price"));

		List<ObjectKeyResult> results = search.search( yearPriceComparator );

		Assert.assertEquals(0, results.get(0).getKey().getId());
		Assert.assertEquals(1, results.get(1).getKey().getId());
		Assert.assertEquals(2, results.get(2).getKey().getId());		 
	}

	@Test
	public void BooleanSearchTwoFieldInverseSort() throws SecurityException, NoSuchFieldException {
		TwoFieldsSortEntity ipod_1998_300usd = new TwoFieldsSortEntity(0,"ipod touch 32 GB",2000, 300);
		TwoFieldsSortEntity ipod_1999_230usd = new TwoFieldsSortEntity(1,"ipod touch 16 GB",1999, 230);
		TwoFieldsSortEntity ipod_2000_200usd = new TwoFieldsSortEntity(2,"ipod touch 8 GB" ,1998, 200);

		Utils.setupSampleMemoryIndex(ipod_1999_230usd, ipod_2000_200usd ,ipod_1998_300usd);

		BooleanSearch search = new BooleanSearch(new BooleanQueryParser("ipod touch").getQuery(), MemoryIndexReaderFactory.getInstance());

		Comparator<ObjectResult> yearPriceComparator =
			new YearPriceComparator(TwoFieldsSortEntity.class.getDeclaredField("year"),
					TwoFieldsSortEntity.class.getDeclaredField("price"));

		List<ObjectKeyResult> results = search.search( yearPriceComparator );

		Assert.assertEquals(2, results.get(0).getKey().getId());
		Assert.assertEquals(1, results.get(1).getKey().getId());
		Assert.assertEquals(0, results.get(2).getKey().getId());		 
	}

	private class MultiClassPriceComparator implements Comparator<ObjectResult> {

		private final Field class1Field, class2Field;

		public MultiClassPriceComparator(Field class1Field, Field class2Field)
		{ this.class1Field = class1Field; this.class2Field = class2Field;}		

		public int compare(ObjectResult o1, ObjectResult o2) {			
			
			float o1Value, o2Value;
			
			if(o1.getKey().getClazz().equals(class1Field.getDeclaringClass()) && o2.getKey().getClazz().equals(class1Field.getDeclaringClass())) {
				o1Value = ((Float)o1.getStoredFields().get(class1Field));
				o2Value = ((Float)o2.getStoredFields().get(class1Field));
			} else if(o1.getKey().getClazz().equals(class1Field.getDeclaringClass()) && o2.getKey().getClazz().equals(class2Field.getDeclaringClass())) {
				o1Value = ((Float)o1.getStoredFields().get(class1Field));
				o2Value = ((Float)o2.getStoredFields().get(class2Field));
			} else if(o1.getKey().getClazz().equals(class2Field.getDeclaringClass()) && o2.getKey().getClazz().equals(class1Field.getDeclaringClass())) {
				o1Value = ((Float)o1.getStoredFields().get(class2Field));
				o2Value = ((Float)o2.getStoredFields().get(class1Field));
			} else 	if(o1.getKey().getClazz().equals(class2Field.getDeclaringClass()) && o2.getKey().getClazz().equals(class2Field.getDeclaringClass())) {
				o1Value = ((Float)o1.getStoredFields().get(class2Field));
				o2Value = ((Float)o2.getStoredFields().get(class2Field));
			} else {
				return 0;				
			}
			
			return Float.compare(o1Value, o2Value);
		}
	}


	@Test
	public void BooleanSearchHeterogeneousClassSameFieldSort() throws SecurityException, NoSuchFieldException {
		TwoFieldsSortEntity ipod_2000_200usd = new TwoFieldsSortEntity(1,"ipod touch 8 GB" ,1998, 200);
		SortEntity ipod_201usd = new SortEntity(2,"ipod touch 32 GB", 201);
		SortEntity ipod_300usd = new SortEntity(0,"ipod touch 32 GB", 300);

		Utils.setupSampleMemoryIndex(ipod_2000_200usd ,ipod_300usd, ipod_201usd);

		BooleanSearch search = new BooleanSearch(new BooleanQueryParser("ipod touch").getQuery(), MemoryIndexReaderFactory.getInstance());

		Comparator<ObjectResult> yearPriceComparator =
			new MultiClassPriceComparator(SortEntity.class.getDeclaredField("price"),
					TwoFieldsSortEntity.class.getDeclaredField("price"));

		List<ObjectKeyResult> results = search.search( yearPriceComparator );

		Assert.assertEquals(1, results.get(0).getKey().getId());
		Assert.assertEquals(2, results.get(1).getKey().getId());
		Assert.assertEquals(0, results.get(2).getKey().getId());
	}
	
	@Test
	public void BooleanSearchHeterogeneousClassInverseFieldSort() throws SecurityException, NoSuchFieldException {
		TwoFieldsSortEntity ipod_2000_200usd = new TwoFieldsSortEntity(1,"ipod touch 8 GB" ,1998, 300);
		SortEntity ipod_201usd = new SortEntity(2,"ipod touch 32 GB", 201);
		SortEntity ipod_300usd = new SortEntity(0,"ipod touch 32 GB", 200);

		Utils.setupSampleMemoryIndex(ipod_2000_200usd ,ipod_300usd, ipod_201usd);

		BooleanSearch search = new BooleanSearch(new BooleanQueryParser("ipod touch").getQuery(), MemoryIndexReaderFactory.getInstance());

		Comparator<ObjectResult> yearPriceComparator =
			new MultiClassPriceComparator(SortEntity.class.getDeclaredField("price"),
					TwoFieldsSortEntity.class.getDeclaredField("price"));

		List<ObjectKeyResult> results = search.search( yearPriceComparator );

		Assert.assertEquals(0, results.get(0).getKey().getId());
		Assert.assertEquals(2, results.get(1).getKey().getId());
		Assert.assertEquals(1, results.get(2).getKey().getId());
	}
	
	@Test
	public void BooleanSearchHeterogeneousClassFieldAscendingSort() throws SecurityException, NoSuchFieldException {
		TwoFieldsSortEntity ipod_2000_200usd = new TwoFieldsSortEntity(1,"ipod touch 8 GB" ,1998, 300);
		SortEntity ipod_201usd = new SortEntity(2,"ipod touch 32 GB", 201);
		SortEntity ipod_300usd = new SortEntity(0,"ipod touch 32 GB", 200);

		Utils.setupSampleMemoryIndex(ipod_2000_200usd ,ipod_300usd, ipod_201usd);

		BooleanSearch search = new BooleanSearch(new BooleanQueryParser("ipod touch").getQuery(), MemoryIndexReaderFactory.getInstance());

		Comparator<ObjectResult> yearPriceComparator =
			new MultiClassPriceComparator(SortEntity.class.getDeclaredField("price"),
					TwoFieldsSortEntity.class.getDeclaredField("price"));

		Comparator<ObjectResult> reverseComparator = new ReverseComparator<ObjectResult>(yearPriceComparator);
		
		List<ObjectKeyResult> results = search.search( reverseComparator );

		Assert.assertEquals(1, results.get(0).getKey().getId());
		Assert.assertEquals(2, results.get(1).getKey().getId());
		Assert.assertEquals(0, results.get(2).getKey().getId());
	}

	// -- Vectorial
	
	@Test
	public void VectorSearchOneFieldSort() throws SecurityException, NoSuchFieldException {
		SortEntity ipod300usd = new SortEntity(0,"ipod touch 32 GB", 300);
		SortEntity ipod230usd = new SortEntity(1,"ipod touch 16 GB", 230);
		SortEntity ipod200usd = new SortEntity(2,"ipod touch 8 GB", 200);

		Utils.setupSampleMemoryIndex(ipod230usd, ipod200usd ,ipod300usd);

		Search search = new VectorSearch(new VectorQueryParser("ipod touch").getQuery(), MemoryIndexReaderFactory.getInstance());

		Comparator<ObjectResult> priceComparator = new PriceComparator(SortEntity.class.getDeclaredField("price"));

		@SuppressWarnings("unchecked")
		List<VectorRankedResult> results = (List<VectorRankedResult>) search.search( priceComparator );

				
		Assert.assertEquals(2, results.get(0).getKey().getId());
		Assert.assertEquals(1, results.get(1).getKey().getId());
		Assert.assertEquals(0, results.get(2).getKey().getId());		 
	}

	@Test
	public void VectorSearchOneFieldInverseSort() throws SecurityException, NoSuchFieldException {
		SortEntity ipod300usd = new SortEntity(0,"ipod touch 32 GB", 200);
		SortEntity ipod230usd = new SortEntity(1,"ipod touch 16 GB", 230);
		SortEntity ipod200usd = new SortEntity(2,"ipod touch 8 GB", 300);

		Utils.setupSampleMemoryIndex(ipod230usd, ipod200usd ,ipod300usd);

		Search search = new VectorSearch(new VectorQueryParser("ipod touch").getQuery(), MemoryIndexReaderFactory.getInstance());

		Comparator<ObjectResult> priceComparator = new PriceComparator(SortEntity.class.getDeclaredField("price"));

		@SuppressWarnings("unchecked")
		List<VectorRankedResult> results = (List<VectorRankedResult>) search.search( priceComparator );

		Assert.assertEquals(0, results.get(0).getKey().getId());
		Assert.assertEquals(1, results.get(1).getKey().getId());
		Assert.assertEquals(2, results.get(2).getKey().getId());		 
	}

	@Test
	public void VectorSearchTwoFieldSort() throws SecurityException, NoSuchFieldException {
		TwoFieldsSortEntity ipod_1998_300usd = new TwoFieldsSortEntity(0,"ipod touch 32 GB",1998, 300);
		TwoFieldsSortEntity ipod_1999_230usd = new TwoFieldsSortEntity(1,"ipod touch 16 GB",1999, 230);
		TwoFieldsSortEntity ipod_2000_200usd = new TwoFieldsSortEntity(2,"ipod touch 8 GB" ,2000, 200);

		Utils.setupSampleMemoryIndex(ipod_1999_230usd, ipod_2000_200usd ,ipod_1998_300usd);

		VectorSearch search = new VectorSearch(new VectorQueryParser("ipod touch").getQuery(), MemoryIndexReaderFactory.getInstance());

		Comparator<ObjectResult> yearPriceComparator =
			new YearPriceComparator(TwoFieldsSortEntity.class.getDeclaredField("year"),
					TwoFieldsSortEntity.class.getDeclaredField("price"));

		List<VectorRankedResult> results = search.search( yearPriceComparator );

		Assert.assertEquals(0, results.get(0).getKey().getId());
		Assert.assertEquals(1, results.get(1).getKey().getId());
		Assert.assertEquals(2, results.get(2).getKey().getId());		 
	}

	@Test
	public void VectornSearchTwoFieldInverseSort() throws SecurityException, NoSuchFieldException {
		TwoFieldsSortEntity ipod_1998_300usd = new TwoFieldsSortEntity(0,"ipod touch 32 GB",2000, 300);
		TwoFieldsSortEntity ipod_1999_230usd = new TwoFieldsSortEntity(1,"ipod touch 16 GB",1999, 230);
		TwoFieldsSortEntity ipod_2000_200usd = new TwoFieldsSortEntity(2,"ipod touch 8 GB" ,1998, 200);

		Utils.setupSampleMemoryIndex(ipod_1999_230usd, ipod_2000_200usd ,ipod_1998_300usd);

		VectorSearch search = new VectorSearch(new VectorQueryParser("ipod touch").getQuery(), MemoryIndexReaderFactory.getInstance());

		Comparator<ObjectResult> yearPriceComparator =
			new YearPriceComparator(TwoFieldsSortEntity.class.getDeclaredField("year"),
					TwoFieldsSortEntity.class.getDeclaredField("price"));

		List<VectorRankedResult> results = search.search( yearPriceComparator );

		Assert.assertEquals(2, results.get(0).getKey().getId());
		Assert.assertEquals(1, results.get(1).getKey().getId());
		Assert.assertEquals(0, results.get(2).getKey().getId());		 
	}

	@Test
	public void VectorSearchHeterogeneousClassSameFieldSort() throws SecurityException, NoSuchFieldException {
		TwoFieldsSortEntity ipod_2000_200usd = new TwoFieldsSortEntity(1,"ipod touch 8 GB" ,1998, 200);
		SortEntity ipod_201usd = new SortEntity(2,"ipod touch 32 GB", 201);
		SortEntity ipod_300usd = new SortEntity(0,"ipod touch 32 GB", 300);

		Utils.setupSampleMemoryIndex(ipod_2000_200usd ,ipod_300usd, ipod_201usd);

		VectorSearch search = new VectorSearch(new VectorQueryParser("ipod touch").getQuery(), MemoryIndexReaderFactory.getInstance());

		Comparator<ObjectResult> yearPriceComparator =
			new MultiClassPriceComparator(SortEntity.class.getDeclaredField("price"),
					TwoFieldsSortEntity.class.getDeclaredField("price"));

		List<VectorRankedResult> results = search.search( yearPriceComparator );

		Assert.assertEquals(1, results.get(0).getKey().getId());
		Assert.assertEquals(2, results.get(1).getKey().getId());
		Assert.assertEquals(0, results.get(2).getKey().getId());
	}
	
	@Test
	public void VectorSearchHeterogeneousClassInverseFieldSort() throws SecurityException, NoSuchFieldException {
		TwoFieldsSortEntity ipod_2000_200usd = new TwoFieldsSortEntity(1,"ipod touch 8 GB" ,1998, 300);
		SortEntity ipod_201usd = new SortEntity(2,"ipod touch 32 GB", 201);
		SortEntity ipod_300usd = new SortEntity(0,"ipod touch 32 GB", 200);

		Utils.setupSampleMemoryIndex(ipod_2000_200usd ,ipod_300usd, ipod_201usd);

		BooleanSearch search = new BooleanSearch(new BooleanQueryParser("ipod touch").getQuery(), MemoryIndexReaderFactory.getInstance());

		Comparator<ObjectResult> yearPriceComparator =
			new MultiClassPriceComparator(SortEntity.class.getDeclaredField("price"),
					TwoFieldsSortEntity.class.getDeclaredField("price"));

		List<ObjectKeyResult> results = search.search( yearPriceComparator );

		Assert.assertEquals(0, results.get(0).getKey().getId());
		Assert.assertEquals(2, results.get(1).getKey().getId());
		Assert.assertEquals(1, results.get(2).getKey().getId());
	}
	
	@Test
	public void VectorSearchHeterogeneousClassFieldAscendingSort() throws SecurityException, NoSuchFieldException {
		TwoFieldsSortEntity ipod_2000_200usd = new TwoFieldsSortEntity(1,"ipod touch 8 GB" ,1998, 300);
		SortEntity ipod_201usd = new SortEntity(2,"ipod touch 32 GB", 201);
		SortEntity ipod_300usd = new SortEntity(0,"ipod touch 32 GB", 200);

		Utils.setupSampleMemoryIndex(ipod_2000_200usd ,ipod_300usd, ipod_201usd);

		VectorSearch search = new VectorSearch(new VectorQueryParser("ipod touch").getQuery(), MemoryIndexReaderFactory.getInstance());

		Comparator<ObjectResult> yearPriceComparator =
			new MultiClassPriceComparator(SortEntity.class.getDeclaredField("price"),
					TwoFieldsSortEntity.class.getDeclaredField("price"));

		Comparator<ObjectResult> reverseComparator = new ReverseComparator<ObjectResult>(yearPriceComparator);
		
		List<VectorRankedResult> results = search.search( reverseComparator );

		Assert.assertEquals(1, results.get(0).getKey().getId());
		Assert.assertEquals(2, results.get(1).getKey().getId());
		Assert.assertEquals(0, results.get(2).getKey().getId());
	}

}
