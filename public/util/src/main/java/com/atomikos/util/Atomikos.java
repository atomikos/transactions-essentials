/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.util;

import java.io.InputStream;
import java.util.Properties;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

public final class Atomikos {
	final private static Logger LOGGER = LoggerFactory.createLogger(Atomikos.class);

	private Atomikos() {
		//Nobody can load me !!!
	}
	public final static String VERSION = loadVersion();

	private static String loadVersion() {
		final Properties properties = new Properties();
		try {
			InputStream inStream = Atomikos.class.getClassLoader().getResourceAsStream("META-INF/maven/com.atomikos/atomikos-util/pom.properties");
			properties.load(inStream);
		} catch (Exception e) {
			LOGGER.logWarning("Unable to load version.properties using Util.class.getClassLoader().getResourceAsStream(...)", e);
			return "UNKNOWN";
		}

		return properties.getProperty("version");
	}
	
	public static boolean isEvaluationVersion() {
		return VERSION.endsWith(".EVAL");
	}


}
