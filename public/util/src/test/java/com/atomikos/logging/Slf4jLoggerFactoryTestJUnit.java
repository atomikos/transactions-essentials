/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.logging;

import org.mockito.Mockito;
import org.slf4j.impl.StaticLoggerBinder;


public class Slf4jLoggerFactoryTestJUnit extends AbstractLoggerFactoryTest {
	
	

	public void setUp() {
		LoggerFactory.setLoggerFactoryDelegate(new Slf4JLoggerFactoryDelegate());
		logger = LoggerFactory.createLogger(getClass());	
	}
	
	protected void assertLoggedAsDebug() {
		Mockito.verify(StaticLoggerBinder.mockito).debug(MESSAGE);
	}
	
	protected void assertLoggedAsTrace() {
		Mockito.verify(StaticLoggerBinder.mockito).trace(MESSAGE);
	}
	protected void assertLoggedAsDebugWithException() {
		Mockito.verify(StaticLoggerBinder.mockito).debug(MESSAGE,ERROR);
			
	}
	@Override
	protected void assertLoggedAsTraceWithException() {
		Mockito.verify(StaticLoggerBinder.mockito).trace(MESSAGE,ERROR);
	}
	
	protected void assertLoggedAsInfo() {
		Mockito.verify(StaticLoggerBinder.mockito).info(MESSAGE);
	}

	protected void assertLoggedAsInfoWithException() {
		Mockito.verify(StaticLoggerBinder.mockito).info(MESSAGE,ERROR);
		
	}

	protected void assertLoggedAsWarning() {
		Mockito.verify(StaticLoggerBinder.mockito).warn(MESSAGE);
	}
	
	protected void assertLoggedAsWarningWithException() {
		Mockito.verify(StaticLoggerBinder.mockito).warn(MESSAGE,ERROR);
	}
	
	public void testAssertSlf4jLoggerCreated() {
		assertTrue(logger instanceof Slf4jLogger);
	}

	@Override
	protected void configureLoggingFrameworkWithDebug() {
		Mockito.when(StaticLoggerBinder.mockito.isDebugEnabled()).thenReturn(true);
	}
	
	@Override
	protected void configureLoggingFrameworkWithTrace() {
		Mockito.when(StaticLoggerBinder.mockito.isTraceEnabled()).thenReturn(true);
	}

	@Override
	protected void configureLoggingFrameworkWithInfo() {
		Mockito.when(StaticLoggerBinder.mockito.isInfoEnabled()).thenReturn(true);
	}

  @Override
  protected void assertLoggedAsError() {
    Mockito.verify(StaticLoggerBinder.mockito).error(MESSAGE);
  }

  @Override
  protected void assertLoggedAsErrorWithException() {
    Mockito.verify(StaticLoggerBinder.mockito).error(MESSAGE,ERROR);
  }

  @Override
  protected void configureLoggingFrameworkWithNone() {
    Mockito.when(StaticLoggerBinder.mockito.isErrorEnabled()).thenReturn(false);
  }

  @Override
  protected void configureLoggingFrameworkWithError() {
    Mockito.when(StaticLoggerBinder.mockito.isErrorEnabled()).thenReturn(true);
  }


	

}
