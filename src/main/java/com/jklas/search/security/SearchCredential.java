package com.jklas.search.security;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class SearchCredential {

	private String role;
	
	private final Set<Class<?>> blacklistClass = new HashSet<Class<?>>();
	
	private final Set<Field> blacklistField = new HashSet<Field>();	
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public void addBlacklistClass(Class<?> blacklisted) {
		blacklistClass.add(blacklisted);
	}
	
	public void addBlacklistField(Field field) {
		blacklistField.add(field);
	}
	
	public String getRole() {
		return role;
	}
	
	public boolean isBlacklistedClass(Class<?> testClass) {
		boolean blacklisted = false;
		
		for (Class<?> clazz : blacklistClass) {
			if(clazz.isAssignableFrom(testClass)) return true;
		}
		
		return blacklisted;
	}
}
