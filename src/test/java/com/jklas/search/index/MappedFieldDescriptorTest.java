package com.jklas.search.index;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

import com.jklas.search.SearchEngine;
import com.jklas.search.configuration.MappedFieldDescriptor;
import com.jklas.search.configuration.SearchConfiguration;
import com.jklas.search.engine.Language;
import com.jklas.search.engine.stemming.StemType;
import com.jklas.search.engine.stemming.Stemmer;
import com.jklas.search.engine.stemming.StemmerStrategy;
import com.jklas.search.engine.stemming.snowball.spanishStemmer;
import com.jklas.search.indexer.Transform;

public class MappedFieldDescriptorTest {

	private class Dummy {
		@SuppressWarnings("unused")		
		private Object attribute;		
	}

	@Test
	public void mappedStemmerIsMapped() {
		SearchConfiguration configuration = getNewSearchConfiguration();

		Dummy dummy = new Dummy();

		Field attributeField = getAttributeField(dummy);

		MappedFieldDescriptor attributeFieldDescriptor = new MappedFieldDescriptor(attributeField);

		StemmerStrategy stemmerStrategy = new StemmerStrategy() {

			@Override
			public Stemmer getStemmer(Language language, StemType stemType) {				
				return new spanishStemmer();
			}

		};

		attributeFieldDescriptor.setStemmerStrategy(stemmerStrategy);

		configuration.addEmptyMapping(dummy.getClass()).put(attributeField, attributeFieldDescriptor);		

		Assert.assertSame(stemmerStrategy, configuration.getMapping(dummy.getClass()).getFieldDescriptor(attributeField).getStemmerStrategy());
	}


	@Test
	public void mappedTransformationIsMapped() {
		SearchConfiguration configuration = getNewSearchConfiguration();

		Dummy dummy = new Dummy();

		Field attributeField = getAttributeField(dummy);

		MappedFieldDescriptor attributeFieldDescriptor = new MappedFieldDescriptor(attributeField);

		Transform<?> dummyTransform = new Transform<Object>(){
			@Override
			public String transform(Object e) { return null; }
		};

		attributeFieldDescriptor.setTransformation(dummyTransform);

		configuration.addEmptyMapping(dummy.getClass()).put(attributeField, attributeFieldDescriptor);

		Assert.assertSame(dummyTransform, configuration.getMapping(dummy.getClass()).getFieldDescriptor(attributeField).getTransformation());
	}

	@Test
	public void mappedLanguageIsMapped() {
		SearchConfiguration configuration = getNewSearchConfiguration();

		Dummy dummy = new Dummy();

		Field attributeField = getAttributeField(dummy);

		MappedFieldDescriptor attributeFieldDescriptor = new MappedFieldDescriptor(attributeField);

		Language spanishLanguage = new Language("spanish");

		attributeFieldDescriptor.setLanguage(spanishLanguage);

		configuration.addEmptyMapping(dummy.getClass()).put(attributeField, attributeFieldDescriptor);

		Assert.assertSame(spanishLanguage, configuration.getMapping(dummy.getClass()).getFieldDescriptor(attributeField).getLanguage());
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

	private SearchConfiguration getNewSearchConfiguration() {
		return SearchEngine.getInstance().newConfiguration();	
	}
}
