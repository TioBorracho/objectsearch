package com.jklas.search.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
public @interface SearchCollection {
	IndexReference reference() default IndexReference.SELF;
}
