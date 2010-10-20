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
