package com.atomikos.icatch.config;

import java.util.Properties;

public class ConfigProperties {
	
	private Properties properties;

	public ConfigProperties(Properties properties) {
		if (properties == null) throw new IllegalArgumentException("Properties should not be null");
		this.properties = properties;
	}

	public String getProperty(String name) {
		String ret = properties.getProperty(name);
		if (ret == null) throw new IllegalArgumentException(name);
		ret = ret.trim();
		return ret;
	}

	public void setProperty(String name,
			String value) {
		properties.setProperty(name, value);		
	}
	
	public boolean getAsBoolean(String name) {
		boolean ret = false;
		String retAsString = getProperty(name);
		ret = Boolean.valueOf(retAsString);
		return ret;
	}

	public int getAsInt(String name) {
		String retAsString = getProperty(name);
		return Integer.valueOf(retAsString);
	}

	public long getAsLong(String name) {
		String retAsString = getProperty(name);
		return Long.valueOf(retAsString);
	}

}
