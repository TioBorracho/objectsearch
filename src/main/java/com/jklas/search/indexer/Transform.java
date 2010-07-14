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
