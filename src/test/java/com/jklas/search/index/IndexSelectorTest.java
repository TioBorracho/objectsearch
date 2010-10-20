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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import com.jklas.search.SearchEngine;
import com.jklas.search.configuration.SearchConfiguration;
import com.jklas.search.exception.SearchEngineException;
import com.jklas.search.index.selector.IndexSelector;
import com.jklas.search.index.selector.IndexSelectorByConstant;
import com.jklas.search.index.selector.IndexSelectorByField;
import com.jklas.search.index.selector.IndexSelectorByMethod;

public class IndexSelectorTest {

	@SuppressWarnings("unused")
	private class DummyEntityWithIndexSelectors {		
		private Object fieldIndexSelector = SELECTED_INDEX;		
		
		private final static String SELECTED_INDEX = "IndexA";
		
		public Object getFieldIndexSelector() {
			return fieldIndexSelector;
		}
	}

	@Test
	public void directFieldSelectorTest() {
		SearchConfiguration configuration = getNewConfiguration();

		DummyEntityWithIndexSelectors dummySelectorByField = new DummyEntityWithIndexSelectors(); 

		Field selectorField;
		try {
			selectorField = dummySelectorByField.getClass().getDeclaredField("fieldIndexSelector");
		} catch (SecurityException e) {
			Assert.fail();
			return;
		} catch (NoSuchFieldException e) {
			Assert.fail();
			return;
		}
		
		IndexSelector selectorPorField = new IndexSelectorByField(selectorField);
		
		configuration.addEmptyMapping(DummyEntityWithIndexSelectors.class).setIndexSelector(selectorPorField);
		
		IndexId indexBySelector;
		try {
			indexBySelector = configuration.getMapping(DummyEntityWithIndexSelectors.class).getIndexSelector().selectIndex(dummySelectorByField);
		} catch (SearchEngineException e) {
			Assert.fail();
			return;
		}
		
		Assert.assertEquals(indexBySelector.getIndexName(), DummyEntityWithIndexSelectors.SELECTED_INDEX);	
	}

	private SearchConfiguration getNewConfiguration() {
		return SearchEngine.getInstance().newConfiguration();
	}
	
	@Test
	public void getterFieldSelectorTest() {
		SearchConfiguration configuration = getNewConfiguration();

		DummyEntityWithIndexSelectors dummySelectorByField = new DummyEntityWithIndexSelectors(); 


		Field selectorField;
		try {
			selectorField = dummySelectorByField.getClass().getDeclaredField("fieldIndexSelector");
		} catch (SecurityException e) {
			Assert.fail();
			return;
		} catch (NoSuchFieldException e) {
			Assert.fail();
			return;
		}
		
		IndexSelector selectorPorField = new IndexSelectorByField(selectorField,true);
		
		configuration.addEmptyMapping(DummyEntityWithIndexSelectors.class).setIndexSelector(selectorPorField);
		
		IndexId indexBySelector;
		try {
			indexBySelector = configuration.getMapping(DummyEntityWithIndexSelectors.class).getIndexSelector().selectIndex(dummySelectorByField);
		} catch (SearchEngineException e) {
			Assert.fail();
			return;
		}
		
		Assert.assertEquals(indexBySelector.getIndexName(), DummyEntityWithIndexSelectors.SELECTED_INDEX);
	}
	
	@SuppressWarnings("unused")
	private class DummyEntityWithOneLetterIndexSelectors {		
		private Object a = SELECTED_INDEX;		
		
		private final static String SELECTED_INDEX = "IndexA";
		
		public Object getA() {
			return a;
		}
	}
	
	@Test
	public void getterOneLetterFieldSelectorTest() {
		SearchConfiguration configuration = getNewConfiguration();

		DummyEntityWithOneLetterIndexSelectors dummyWithOneLetterSelectorByField = new DummyEntityWithOneLetterIndexSelectors(); 


		Field selectorField;
		try {
			selectorField = dummyWithOneLetterSelectorByField.getClass().getDeclaredField("a");
		} catch (SecurityException e) {
			Assert.fail();
			return;
		} catch (NoSuchFieldException e) {
			Assert.fail();
			return;
		}
		
		IndexSelector selectorPorField = new IndexSelectorByField(selectorField,true);
		
		configuration.addEmptyMapping(DummyEntityWithOneLetterIndexSelectors.class).setIndexSelector(selectorPorField);
		
		IndexId indexBySelector;
		try {
			indexBySelector = configuration.getMapping(DummyEntityWithOneLetterIndexSelectors.class).getIndexSelector().selectIndex(dummyWithOneLetterSelectorByField);
		} catch (SearchEngineException e) {
			Assert.fail();
			return;
		}
		
		Assert.assertEquals(indexBySelector.getIndexName(), DummyEntityWithOneLetterIndexSelectors.SELECTED_INDEX);
	}
	
	@Test
	public void fieldSelectorByOneLetterFieldNameTest() {
		SearchConfiguration configuration = getNewConfiguration();

		DummyEntityWithOneLetterIndexSelectors dummyWithOneLetterSelectorByField = new DummyEntityWithOneLetterIndexSelectors(); 
	
		IndexSelector selectorPorField;
		try {
			selectorPorField = new IndexSelectorByField(DummyEntityWithOneLetterIndexSelectors.class,"a");
		} catch (SecurityException e1) {
			Assert.fail();
			return;
		} catch (NoSuchFieldException e1) {
			Assert.fail();
			return;
		}
		
		configuration.addEmptyMapping(DummyEntityWithOneLetterIndexSelectors.class).setIndexSelector(selectorPorField);
		
		IndexId indexBySelector;
		try {
			indexBySelector = configuration.getMapping(DummyEntityWithOneLetterIndexSelectors.class).getIndexSelector().selectIndex(dummyWithOneLetterSelectorByField);
		} catch (SearchEngineException e) {
			Assert.fail();
			return;
		}
		
		Assert.assertEquals(indexBySelector.getIndexName(), DummyEntityWithOneLetterIndexSelectors.SELECTED_INDEX);
	}
	
	@Test
	public void fieldSelectorByMultipleLetterFieldNameTest() {
		SearchConfiguration configuration = getNewConfiguration();

		DummyEntityWithIndexSelectors dummySelectorByField = new DummyEntityWithIndexSelectors(); 
	
		IndexSelector selectorPorField;
		try {
			selectorPorField = new IndexSelectorByField(dummySelectorByField.getClass(),"fieldIndexSelector");
		} catch (SecurityException e1) {
			Assert.fail();
			return;
		} catch (NoSuchFieldException e1) {
			Assert.fail();
			return;
		}
		
		configuration.addEmptyMapping(dummySelectorByField.getClass()).setIndexSelector(selectorPorField);
		
		IndexId indexBySelector;
		try {
			indexBySelector = configuration.getMapping(dummySelectorByField.getClass()).getIndexSelector().selectIndex(dummySelectorByField);
		} catch (SearchEngineException e) {
			Assert.fail();
			return;
		}
		
		Assert.assertEquals(indexBySelector.getIndexName(), DummyEntityWithOneLetterIndexSelectors.SELECTED_INDEX);
	}
	
	@SuppressWarnings("unused")
	private class DummyEntityWithMethodIndexSelectors {		
		
		private final static String SELECTED_INDEX = "IndexA";
		
		public Object selectorMethod() {
			return SELECTED_INDEX;
		}
	}
	
	@Test
	public void directMethodSelectorTest() {
		SearchConfiguration configuration = getNewConfiguration();

		DummyEntityWithMethodIndexSelectors dummy = new DummyEntityWithMethodIndexSelectors();
		
		Method method;
		try {
			method = dummy.getClass().getDeclaredMethod("selectorMethod", new Class<?>[]{});
		} catch (SecurityException e) {
			Assert.fail();
			return;
		} catch (NoSuchMethodException e) {
			Assert.fail();
			return;
		}
		
		IndexSelector methodSelector = new IndexSelectorByMethod(method);
		
		configuration.addEmptyMapping(DummyEntityWithMethodIndexSelectors.class).setIndexSelector(methodSelector);
		
		try {
			Assert.assertEquals(methodSelector.selectIndex(dummy).getIndexName(), DummyEntityWithOneLetterIndexSelectors.SELECTED_INDEX);
		} catch (SearchEngineException e) {
			Assert.fail();
			return;
		}
	}
	
	@Test
	public void methodNameSelectorTest() {
		SearchConfiguration configuration = getNewConfiguration();

		DummyEntityWithMethodIndexSelectors dummy = new DummyEntityWithMethodIndexSelectors();
		
		IndexSelector methodSelector;
		try {
			methodSelector = new IndexSelectorByMethod(dummy.getClass(),"selectorMethod");
		} catch (SecurityException e1) {
			Assert.fail();
			return;
		} catch (NoSuchMethodException e1) {
			Assert.fail();
			return;
		}
		
		configuration.addEmptyMapping(DummyEntityWithMethodIndexSelectors.class).setIndexSelector(methodSelector);
		
		try {
			Assert.assertEquals(methodSelector.selectIndex(dummy).getIndexName(), DummyEntityWithOneLetterIndexSelectors.SELECTED_INDEX);
		} catch (SearchEngineException e) {
			Assert.fail();
			return;
		}
	}
	
	private class DummyEntity { }

	@Test
	public void constantMethodSelector() {
		SearchConfiguration configuration = getNewConfiguration();

		DummyEntity dummy = new DummyEntity();
		
		String selectedIndex = "indexA";
		
		IndexSelector constantSelector = new IndexSelectorByConstant(selectedIndex);
		
		configuration.addEmptyMapping(DummyEntityWithMethodIndexSelectors.class).setIndexSelector(constantSelector);
		
		try {
			Assert.assertEquals(constantSelector.selectIndex(dummy).getIndexName(), selectedIndex);
		} catch (SearchEngineException e) {
			Assert.fail();
			return;
		}
	}
	
}
