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
package com.jklas.search.engine.stemming.snowball;

import java.lang.reflect.Method;

public class Among {
    public Among (String s, int substring_i, int result,
		  String methodname, SnowballProgram methodobject) {
        this.s_size = s.length();
        this.s = s.toCharArray();
        this.substring_i = substring_i;
	this.result = result;
	this.methodobject = methodobject;
	if (methodname.length() == 0) {
	    this.method = null;
	} else {
	    try {
		this.method = methodobject.getClass().
		getDeclaredMethod(methodname, new Class[0]);
	    } catch (NoSuchMethodException e) {
		throw new RuntimeException(e);
	    }
	}
    }

    public final int s_size; /* search string */
    public final char[] s; /* search string */
    public final int substring_i; /* index to longest matching substring */
    public final int result; /* result of the lookup */
    public final Method method; /* method to use if substring matches */
    public final SnowballProgram methodobject; /* object to invoke method on */
};
