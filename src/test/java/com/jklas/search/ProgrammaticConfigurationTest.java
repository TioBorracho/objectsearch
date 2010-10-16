package com.jklas.search;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

import com.jklas.search.configuration.MappedFieldDescriptor;
import com.jklas.search.configuration.SearchConfiguration;
import com.jklas.search.index.selector.IndexSelector;
import com.jklas.search.index.selector.IndexSelectorByConstant;

public class ProgrammaticConfigurationTest {

	private class DummyEntity { }
		
	@Test
	public void testNewProgrammaticConfiguresSearch() {
		getNewSearchConfiguration();		
				
		Assert.assertTrue(SearchEngine.getInstance().isConfigured());
	}

	private SearchConfiguration getNewSearchConfiguration() {
		return SearchEngine.getInstance().newConfiguration();	
	}
	
	@Test
	public void testNewConfigurationReplacesOldConfiguration() {
		SearchConfiguration firstConfiguration = getNewSearchConfiguration();
		
		SearchConfiguration secondConfiguration = getNewSearchConfiguration();
		
		Assert.assertNotSame(firstConfiguration,secondConfiguration);
		
		Assert.assertNotSame(firstConfiguration,SearchEngine.getInstance().getConfiguration());		
	}
	
	@Test
	public void testMappedClassIsMapped() {
		SearchConfiguration configuration = getNewSearchConfiguration();

		DummyEntity dummy = new DummyEntity(); 

		configuration.addEmptyMapping(DummyEntity.class);		

		Assert.assertTrue(configuration.isMapped(DummyEntity.class));

		Assert.assertTrue(configuration.isMapped(dummy));
	}

	@Test
	public void testUnmappedClassIsNotMapped() {
		SearchConfiguration configuration = getNewSearchConfiguration();

		DummyEntity dummy = new DummyEntity(); 	

		Assert.assertFalse(configuration.isMapped(DummyEntity.class));

		Assert.assertFalse(configuration.isMapped(dummy));
	}

	private class DummyEntityWithAttribute {		
		@SuppressWarnings("unused")
		private Object attribute;		
	}
	
	@Test
	public void testMappedAttributeIsMapped() {
		SearchConfiguration configuration = getNewSearchConfiguration();

		DummyEntityWithAttribute dummyWithAttributes = new DummyEntityWithAttribute(); 
			
		Field attributeField = getAttributeField(dummyWithAttributes);
		
		configuration.addEmptyMapping(dummyWithAttributes.getClass()).put(attributeField, new MappedFieldDescriptor(attributeField));
			
		Assert.assertTrue(configuration.isMapped(dummyWithAttributes.getClass(),attributeField));
		
	}

	@Test
	public void testUnmappedAttributeIsNotInMapping() {
		SearchConfiguration configuration = getNewSearchConfiguration();

		DummyEntityWithAttribute dummyWithAtributes = new DummyEntityWithAttribute(); 

		Assert.assertFalse(configuration.isMapped(dummyWithAtributes.getClass(),getAttributeField(dummyWithAtributes)));
	}

	@Test
	public void testSelectorIsMapped() {
		SearchConfiguration configuration = getNewSearchConfiguration();
		
		IndexSelector selector = new IndexSelectorByConstant("index");
		
		configuration.addEmptyMapping(DummyEntity.class).setIndexSelector(selector);
				
		IndexSelector selectorRecuperado = configuration.getMapping(DummyEntity.class).getIndexSelector();
		
		Assert.assertEquals(selector, selectorRecuperado);		
	}

		
	@Test
	public void testFieldDescriptorIsMapped() {
		SearchConfiguration configuration = getNewSearchConfiguration();
		
		DummyEntityWithAttribute dummy = new DummyEntityWithAttribute();
		
		Field fieldToBeIndexed = getAttributeField(dummy);
		
		MappedFieldDescriptor fieldInfo = new MappedFieldDescriptor(fieldToBeIndexed);
		
		configuration.addEmptyMapping(dummy.getClass()).put(fieldToBeIndexed, fieldInfo);
		
		Assert.assertEquals(fieldInfo,configuration.getMapping(dummy.getClass()).getFieldDescriptor(fieldToBeIndexed));
	}

	private Field getAttributeField(Object dummy) {
		Field fieldToBeIndexed;
		try {
			fieldToBeIndexed = dummy.getClass().getDeclaredField("attribute");
		} catch (SecurityException e) {
			Assert.fail("This class shouldn't be used for this test, it throws a SecurityException");
			throw new RuntimeException();
		} catch (NoSuchFieldException e) {
			Assert.fail("Trying to index a field that doesn't exists on this class");
			throw new RuntimeException();
		}
		return fieldToBeIndexed;
	}
	
}
