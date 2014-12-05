package com.pj.magic;

import java.util.ResourceBundle;

public class ApplicationProperties {

	private static final ResourceBundle properties = ResourceBundle.getBundle("application");
	
	public static String getProperty(String propertyName) {
		return properties.getString(propertyName);
	}
	
}