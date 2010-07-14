package com.jklas.search.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jklas.search.indexer.IdentityTransform;
import com.jklas.search.indexer.Transform;

/**
 * <p>
 * Anotación que indica que el campo anotado
 * debe ser indexado por el Search Engine.
 * </p>
 * 
 * @author Julián
 * @date 2009-07-26
 * @since 1.0
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SearchField {	
	boolean accessByGet() default false;	
	Class<? extends Transform<?>> transform() default IdentityTransform.class;	
}
