package com.jklas.search.index.selector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.jklas.search.exception.SearchEngineException;
import com.jklas.search.index.IndexId;

public class IndexSelectorByField extends IndexSelector {

	private final Field selectorField;

	private final boolean accessByGet;

	private final Method fieldGetter; 

	public IndexSelectorByField(Field selectorField) {
		this.selectorField = selectorField;
		this.selectorField.setAccessible(true);
		this.accessByGet = false;
		this.fieldGetter = null;
	}

	public IndexSelectorByField(Field selectorField, boolean accessByGet) throws IllegalArgumentException {
		this.selectorField = selectorField;
		this.accessByGet = accessByGet;
		this.fieldGetter = constructFieldGetter(selectorField);
	}

	public IndexSelectorByField(Class<?> clazz, String fieldName) throws SecurityException, NoSuchFieldException {		
		this(clazz.getDeclaredField(fieldName));
	}

	private Method constructFieldGetter(Field field) {
		Class<?> selectorFieldDeclaringClass = selectorField.getDeclaringClass();
		String fieldName = field.getName();
		String methodName = "get"+Character.toUpperCase(fieldName.charAt(0));
		if(fieldName.length()>1) methodName += fieldName.substring(1);
		
		try {			
			Method fieldGetter = selectorFieldDeclaringClass.getDeclaredMethod(methodName);

			if(!fieldGetter.getReturnType().isAssignableFrom(String.class)) {
				throw new IllegalArgumentException(fieldGetter.getDeclaringClass().toString()+"."+fieldGetter.getName()+" return supertype must be String");
			}

			fieldGetter.setAccessible(true);
			return fieldGetter;
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Method "+methodName+" isn't accesible",e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Class "+field.getDeclaringClass().toString()+" doesn't have a "+methodName+" method",e);
		}
	}

	@Override
	public IndexId selectIndex(Object object) throws SearchEngineException {
		if(accessByGet) {
			try {
				return new IndexId(fieldGetter.invoke(object, new Object[]{}).toString());
			} catch (IllegalArgumentException e) {		
				throw new SearchEngineException("While selecting index for object"+object+" - Method "+fieldGetter.getDeclaringClass().toString()+"."+fieldGetter.getName()+" shouldn't take arguments",e);
			} catch (IllegalAccessException e) {
				throw new SearchEngineException("While selecting index for object"+object+" - Method "+fieldGetter.getDeclaringClass().toString()+"."+fieldGetter.getName()+" not accesible (event when setAccessible was set to true)",e);
			} catch (InvocationTargetException e) {
				throw new SearchEngineException("While selecting index for object"+object+" - Method "+fieldGetter.getDeclaringClass().toString()+"."+fieldGetter.getName()+" threw an exception",e);
			}
		} else {
			try {				
				return new IndexId(selectorField.get(object).toString());
			} catch (IllegalArgumentException e) {
				throw new SearchEngineException("While selecting index for object"+object+" - Field "+selectorField.getDeclaringClass().toString()+"."+selectorField.getName(),e);
			} catch (IllegalAccessException e) {
				throw new SearchEngineException("While selecting index for object"+object+" - Field "+selectorField.getDeclaringClass().toString()+"."+selectorField.getName(),e);
			}
		}
	}

}
