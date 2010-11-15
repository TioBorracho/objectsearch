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
package com.jklas.search.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jklas.search.engine.processor.DefaultObjectTextProcessor;
import com.jklas.search.engine.processor.ObjectTextProcessor;

/**
 * This annotation states which text processor
 * will work on this object or field.
 * 
 * @author Julián Klas
 *
 */
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
