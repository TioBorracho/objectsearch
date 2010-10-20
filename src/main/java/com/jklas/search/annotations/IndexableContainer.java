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
