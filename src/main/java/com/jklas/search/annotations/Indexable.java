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

import com.jklas.search.index.IndexId;


/**
 * <p>
 * This annotation states that the objects of this
 * type will be indexed by the search engine.
 * </p>
 * 
 * @author Julián Klas (jklas@fi.uba.ar)
 * @since 1.0
 * @date 2009-07-26
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Indexable {	
	
	/**
	 * The name of the index where objects will be indexed.
	 * 
	 * Note that if you also specify an {@link IndexSelector},
	 * the search engine will use that index selector instead
	 * of this property. 
	 * 
	 * @return a string representing the index id
	 */
	String indexName() default IndexId.DEFAULT_INDEX_NAME;
	
	/**
	 * <p>
	 * Sets the expected indexing behavior when indexing a class that has subclasses.
	 * </p>
	 * 
	 * <p>
	 * This property is checked when a subclass is not
	 * annotated with the @Indexable annotation.
	 * </p>
	 * 
	 * <p>
	 * When set to true, the subclasses without an @Indexable annotation will
	 * be indexed as if they had this annotation.
	 * </p>
	 * 
	 * <p>
	 * When set to false, the subclasses without an @Indexable annotation
	 * won't be indexed at all.
	 * </p>
	 * 
	 * @return a boolean value determining the indexing behavior for subclasses. 
	 */
	boolean makeSubclassesIndexable() default false;

	/**
	 * <p>
	 * If the Search Engine should search for attributes on superclasses,
	 * this attribute must be set to the top class in hierarchy to be included.
	 * </p>
	 * <p>
	 * It's perfectly legal to specify Object.class as the climbing target.
	 * </p>
	 * <p>
	 * Be careful when combining this property with "indexCascading" property.
	 * 
	 * The only legal way to combine these two parameters is to non overlap the
	 * paths of these two properties. 
	 * </p>
	 * <p>
	 * If you specify a target that is not in the subtree from the Object.class
	 * to the annotated class, all classes in the path will be scanned (as if you
	 * specify Object.class as the climbing target).
	 * </p>
	 */
	Class<? extends Object> climbingTarget() default NoClimbing.class;
	
	public class NoClimbing{}
}
