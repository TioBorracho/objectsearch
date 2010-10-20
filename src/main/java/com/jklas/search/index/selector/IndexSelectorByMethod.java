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
package com.jklas.search.index.selector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.jklas.search.exception.SearchEngineException;
import com.jklas.search.index.IndexId;

public class IndexSelectorByMethod extends IndexSelector {

	private Method selectorMethod;
	
	public IndexSelectorByMethod(Method selectorMethod) {
		this.selectorMethod = selectorMethod;
		this.selectorMethod.setAccessible(true);
	}

	public IndexSelectorByMethod(Class<?> clazz, String methodName) throws SecurityException, NoSuchMethodException {		
		this(clazz.getDeclaredMethod("selectorMethod", new Class<?>[]{}));		
	}

	@Override
	public IndexId selectIndex(Object object) throws SearchEngineException {
		try {
			return new IndexId((String) selectorMethod.invoke(object, new Object[]{}));
		} catch (IllegalArgumentException e) {		
			throw new SearchEngineException("While selecting index for object"+object+" - Method "+selectorMethod.getName()+" shouldn't take arguments",e);
		} catch (IllegalAccessException e) {
			throw new SearchEngineException("While selecting index for object"+object+" - Method "+selectorMethod.getName()+" not accesible (event when setAccessible was set to true)",e);
		} catch (InvocationTargetException e) {
			throw new SearchEngineException("While selecting index for object"+object+" - Method "+selectorMethod.getName()+" threw an exception",e);
		}		
	}
}
