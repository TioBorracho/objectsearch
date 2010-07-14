package com.jklas.search.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jklas.search.engine.stemming.IdentityStemmerStrategy;
import com.jklas.search.engine.stemming.StemType;
import com.jklas.search.engine.stemming.StemmerStrategy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Stemming {
	Class<? extends StemmerStrategy> strategy() default IdentityStemmerStrategy.class;
	StemType stemType() default StemType.NO_STEM; 
}
