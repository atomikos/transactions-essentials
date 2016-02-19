/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
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
	
	protected void assertLoggedAsDebugWithException() {
		Mockito.verify(StaticLoggerBinder.mockito).debug(MESSAGE,ERROR);
			
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
