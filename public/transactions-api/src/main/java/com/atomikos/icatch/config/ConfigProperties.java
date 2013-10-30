package com.atomikos.icatch.config;

import java.util.Properties;

public class ConfigProperties {
	
	private Properties properties;

	public ConfigProperties(Properties properties) {
		if (properties == null) throw new IllegalArgumentException("Properties should not be null");
		this.properties = properties;
	}

	public String getProperty(String name) {
		return properties.getProperty(name);
	}

	public void setProperty(String name,
			String value) {
		properties.setProperty(name, value);		
	}

}
