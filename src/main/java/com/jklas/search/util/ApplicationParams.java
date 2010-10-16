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
