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
package com.jklas.search.indexer;

/**
 * <p>
 * This interface must be implemented to provide 
 * a custom interpretation of some field content.
 * </p>
 * <p>
 * If no custom transformation is provided, the
 * Search Engine will execute the toString() method
 * to access the field content.
 * </p>
 * <p>
 * This mechanism allows the toString() method to behave
 * correctly in the indexing and application contexts.
 * </p>
 * <b>Important</b>: transform classes must be stateless.
 * 
 * 
 * @author Julián Klas
 * @since 1.0
 * @date 2009-08-02
 * @param <E> clase del campo para el cual se provee la transformación
 */
public abstract class Transform<E> {	
		
	public abstract Object transform(E e);
	
	public final boolean equals(Object other) {		
		return other.getClass()==getClass();
	}
}
