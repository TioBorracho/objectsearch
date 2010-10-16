package com.jklas.search.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jklas.search.engine.processor.DefaultObjectTextProcessor;
import com.jklas.search.engine.processor.ObjectTextProcessor;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.FIELD})
public @interface TextProcessor {	
		/**
	 * Sets the default text processor for the fields of this class
	 * 
	 * Note that this can be overriden by the {@link SearchField} annotation.
	 * 
	 * @return a class that will be used for text processing
	 * of the fields of this type
	 */
	Class<? extends ObjectTextProcessor> value() default DefaultObjectTextProcessor.class;
}
