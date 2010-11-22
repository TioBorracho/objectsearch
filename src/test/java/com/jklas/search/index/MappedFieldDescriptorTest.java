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

import org.junit.Assert;
import org.junit.Test;

import com.jklas.search.SearchEngine;
import com.jklas.search.configuration.MappedFieldDescriptor;
import com.jklas.search.configuration.SearchConfiguration;
import com.jklas.search.engine.Language;
import com.jklas.search.engine.stemming.StemType;
import com.jklas.search.engine.stemming.Stemmer;
import com.jklas.search.engine.stemming.StemmerStrategy;
import com.jklas.search.engine.stemming.snowball.SpanishStemmer;
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
				return new SpanishStemmer();
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
