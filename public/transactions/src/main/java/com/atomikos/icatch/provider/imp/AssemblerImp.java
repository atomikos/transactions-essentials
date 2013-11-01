package com.atomikos.icatch.provider.imp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import com.atomikos.icatch.provider.Assembler;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.ClassLoadingHelper;

public class AssemblerImp implements Assembler {
	
	private static final String DEFAULT_PROPERTIES_FILE_NAME = "transactions-defaults.properties";

	private static final String JTA_PROPERTIES_FILE_NAME = "jta.properties";

	private static final String TRANSACTIONS_PROPERTIES_FILE_NAME = "transactions.properties";

	private static final String FILE_PATH_PROPERTY_NAME = "com.atomikos.icatch.file";
	
	private static com.atomikos.logging.Logger LOGGER = LoggerFactory.createLogger(AssemblerImp.class);
	
    private void loadPropertiesFromClasspath(Properties p, String fileName){
    		URL url = null;
    		
    		//first look in application classpath (cf ISSUE 10091)
    		url = ClassLoadingHelper.loadResourceFromClasspath(getClass(), fileName);		
    		if (url == null) {
    			url = getClass().getClassLoader().getSystemResource ( fileName );
    		}
    		if (url != null) {
    			loadPropertiesFromUrl(p, url);
    		} else {
    			LOGGER.logDebug("Could not find expected property file: " + fileName);
    		}
    }

	private void loadPropertiesFromUrl(Properties p, URL url) {
		InputStream in;
		try {
			in = url.openStream();
			p.load(in);
			in.close();
		} catch (IOException e) {
			LOGGER.logDebug("Failed to load property file: " + url.toString(), e);
		}
	}

	/**
	 * Called by ServiceLoader.
	 */
	public AssemblerImp() {
	}

	@Override
	public ConfigProperties initializeProperties() {
		Properties defaults = new Properties();
		loadPropertiesFromClasspath(defaults, DEFAULT_PROPERTIES_FILE_NAME);
		Properties transactionsProperties = new Properties(defaults);
		loadPropertiesFromClasspath(transactionsProperties, TRANSACTIONS_PROPERTIES_FILE_NAME);
		Properties jtaProperties = new Properties(transactionsProperties);
		loadPropertiesFromClasspath(jtaProperties, JTA_PROPERTIES_FILE_NAME);
		Properties customProperties = new Properties(jtaProperties);
		loadPropertiesFromCustomFilePath(customProperties);
		Properties finalProperties = new Properties(customProperties);
		applySystemProperties(finalProperties);
		ConfigPropertiesUtils.substitutePlaceHolderValues(finalProperties);
		return new ConfigProperties(finalProperties);
	}

	private void loadPropertiesFromCustomFilePath(Properties customProperties) {
		String customFilePath = System.getProperty(FILE_PATH_PROPERTY_NAME);
		if (customFilePath != null) {
			File file = new File(customFilePath);
			URL url;
			try {
				url = file.toURL();
				loadPropertiesFromUrl(customProperties, url);
			} catch (MalformedURLException e) {
				LOGGER.logWarning("File not found: " + customFilePath);
			}
		}
	}

	private void applySystemProperties(Properties finalProperties) {
		Properties systemProperties = System.getProperties();
		Enumeration<?> propertyNames = systemProperties.propertyNames();
		while (propertyNames.hasMoreElements()) {
			String name = (String) propertyNames.nextElement();
			if (name.startsWith("com.atomikos")) {
				finalProperties.setProperty(name, systemProperties.getProperty(name));
			}
		}
	}
}
