package com.jklas.search.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * This annotation states that the objects of this
 * type will NOT be indexed by the search engine,
 * even when super classes are indexable and specifies 
 * cascading.
 * </p>
 * 
 * @author Julián Klas (jklas@fi.uba.ar)
 * @since 1.0
 * @date 2010-18-08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NotIndexable {

}
