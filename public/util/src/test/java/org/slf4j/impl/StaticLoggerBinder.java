/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package org.slf4j.impl;

import org.mockito.Mockito;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.spi.LoggerFactoryBinder;

public class StaticLoggerBinder implements LoggerFactoryBinder {
	
	public static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

	public static Logger mockito;

	public ILoggerFactory getLoggerFactory() {
		return new ILoggerFactory() {

			public Logger getLogger(String clazz) {
				mockito = Mockito.mock(Logger.class);
				return mockito;
			}
			
		};
	}

	public String getLoggerFactoryClassStr() {
		return null;
	}

}
