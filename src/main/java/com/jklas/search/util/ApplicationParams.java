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
package com.jklas.search.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class ApplicationParams {

	public enum ParamType {SEARCH_PROPERTY};
	
	private static Properties searchProperties;
	
	static {
		loadSearchProperties();
	}
	
	public static String getParameter(ParamType type, String paramName) {
		if(type == ParamType.SEARCH_PROPERTY) {
			return searchProperties.getProperty(paramName);
		}
		return null;
	}

	private static void loadSearchProperties() {
		searchProperties = new Properties();
		try {
			File f = new File("search_files/search_properties.prop");
			searchProperties.load(new FileInputStream(f));
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}
	}
	
}
