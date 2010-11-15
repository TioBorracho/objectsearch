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
 * This annotations marks some class as a container
 * of another class that we want to index.
 * 
 * This annotation must be used when interceptor
 * doesn't have access to the object that we
 * want to index but it has access to a container object (DTO).
 * 
 * </p>
 * 
 * <p>
 * This annotation states that the contained object
 * is the one that must be indexed. The enclosing
 * object won't be indexed.
 * </p>
 * 
 * <p>
 * The effect is the same that you'd get annotating
 * the contained object as @Indexbale
 * 
 * It's required that the contained object is annotated as @Indexbale 
 * </p>
 * 
 * @author Julián Klas
 * @since 1.0
 * @date 2009-07-26
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IndexableContainer { }
