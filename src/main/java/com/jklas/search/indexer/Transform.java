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
 * Esta interfaz debe ser implementada por el usuario 
 * del framework para proveer un método propio de  
 * intepretación de un campo indexable.
 * </p>
 * <p>
 * Si no se provee un método de transformación, el 
 * indexador utilizará el método toString()
 * del campo que se está indexando.
 * </p>
 * <p>
 * Este mecanismo permite que un objeto tenga un
 * comportamiento para el método toString()
 * en la aplicación donde se creó y otro comportamiendo
 * para el Search Engine.
 * </p>
 * <b>IMPORTANTE</b>: las clases transformadoras permanecen en 
 * <b>cache</b>, por lo que no deben tener estado conversacional.
 * Todo su estado se debe cargar en el constructor.
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
