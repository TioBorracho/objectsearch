package com.jklas.search.engine.filter;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.jklas.search.engine.dto.ObjectResult;

/**
 * Aplica las credenciales configuradas a los resultados, eliminando los objetos
 * descendientes de una clase del Blacklist.
 * 
 * @author Julián Klas
 * @since 1.0
 * @date 2009-07-26
 *
 */
public class SecurityFilter implements ResultFilter {

	@Override
	public boolean isFiltered(ObjectResult filtrable) {
		throw new NotImplementedException();
	}
}
