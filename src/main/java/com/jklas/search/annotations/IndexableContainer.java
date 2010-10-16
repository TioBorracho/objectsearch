package com.jklas.search.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Anotación que marca que una clase es contenedora
 * de una clase que queremos indexar.
 * 
 * Esta anotación
 * debe ser usada cuando el interceptor no tiene acceso
 * al objeto que realmente queremos indexar sino
 * a un Data Transfer Object que contiene al objeto que
 * queremos indexar.
 * </p>
 * 
 * <p>
 * Esta anotación indica que se debe indexar la clase
 * contenida. No se indexa la clase contenedora.
 * </p>
 * 
 * <p>
 * El efecto es el mismo que si marcara como indexable
 * la clase contenida. Es necesario que el objeto
 * contenido esté marcado como indexable con el 
 * annotation correspondiente.
 * </p>
 * 
 * @author Julián
 * @since 1.0
 * @date 2009-07-26
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IndexableContainer { }
