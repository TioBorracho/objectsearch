package com.jklas.search.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation must be used to mark a field as a filter field.
 * 
 * Fields annotated as IndexFilter can be filtered when
 * retrieving objects from inverted indexes.
 * 
 * The annotated field must be Serializable since it's content
 * must be stored.
 * 
 * If you want to filter after the hydration process,
 * you shouldn't use this annotation
 * 
 * @author Julián
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SearchFilter {
	boolean accessByGet() default false;	
}
