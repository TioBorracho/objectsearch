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
