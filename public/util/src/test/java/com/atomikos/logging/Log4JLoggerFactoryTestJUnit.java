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

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class Log4JLoggerFactoryTestJUnit extends AbstractLoggerFactoryTest {

	//can't mock the "logger" Itself but Logger call a "publish" method on a mocked handler
	private Appender mockedAppender = Mockito.mock(Appender.class);

	public void setUp() {
		LoggerFactory.setLoggerFactoryDelegate(new Log4JLoggerFactoryDelegate());
		logger = LoggerFactory.createLogger(getClass());

		getUnderlyingLogger().addAppender(mockedAppender);
		getUnderlyingLogger().setLevel(Level.INFO);
	}

	public void testAssertSlf4jLoggerCreated() {
		assertTrue(logger instanceof Log4JLogger);
	}

	public void testLogDebug() {
		configureLoggingFrameworkWithDebug();
		logger.logDebug(MESSAGE);
		assertLoggedAsDebug();
	}

	@Override
	protected void assertLoggedAsDebug() {
		ArgumentCaptor<LoggingEvent> arg = ArgumentCaptor.forClass(LoggingEvent.class);
		Mockito.verify(mockedAppender).doAppend(arg.capture());
		LoggingEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent,Level.DEBUG,false);
	}

	@Override
	protected void assertLoggedAsDebugWithException() {
		ArgumentCaptor<LoggingEvent> arg = ArgumentCaptor.forClass(LoggingEvent.class);
		Mockito.verify(mockedAppender).doAppend(arg.capture());
		LoggingEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent,Level.DEBUG,true);
	}

	private void assertLoggingEvent(LoggingEvent loggingEvent,Level expectedLevel,boolean withThrowable) {
		assertEquals(MESSAGE, loggingEvent.getMessage());
		assertEquals(expectedLevel, loggingEvent.getLevel());
		if(withThrowable){
			assertNotNull(loggingEvent.getThrowableInformation());
		} else {
			assertNull(loggingEvent.getThrowableInformation());
		}

	}

	@Override
	protected void assertLoggedAsInfo() {
		ArgumentCaptor<LoggingEvent> arg = ArgumentCaptor.forClass(LoggingEvent.class);
		Mockito.verify(mockedAppender).doAppend(arg.capture());
		LoggingEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent,Level.INFO,false);
	}

	@Override
	protected void assertLoggedAsInfoWithException() {
		ArgumentCaptor<LoggingEvent> arg = ArgumentCaptor.forClass(LoggingEvent.class);
		Mockito.verify(mockedAppender).doAppend(arg.capture());
		LoggingEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent,Level.INFO,true);

	}

	@Override
	protected void assertLoggedAsWarning() {
		ArgumentCaptor<LoggingEvent> arg = ArgumentCaptor.forClass(LoggingEvent.class);
		Mockito.verify(mockedAppender).doAppend(arg.capture());
		LoggingEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent,Level.WARN,false);
		}

	@Override
	protected void assertLoggedAsWarningWithException() {
		ArgumentCaptor<LoggingEvent> arg = ArgumentCaptor.forClass(LoggingEvent.class);
		Mockito.verify(mockedAppender).doAppend(arg.capture());
		LoggingEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent,Level.WARN,true);
	}

	public void testIsInfoEnabled() {
		getUnderlyingLogger().setLevel(Level.WARN);
		super.testIsInfoEnabled();
	}

	@Override
	protected void configureLoggingFrameworkWithInfo() {
		getUnderlyingLogger().setLevel(Level.INFO);

	}

	@Override
	protected void configureLoggingFrameworkWithDebug() {
		getUnderlyingLogger().setLevel(Level.DEBUG);

	}

	private org.apache.log4j.Logger getUnderlyingLogger() {
		return org.apache.log4j.Logger.getLogger(getClass());
	}

  @Override
  protected void assertLoggedAsError() {
    ArgumentCaptor<LoggingEvent> arg = ArgumentCaptor.forClass(LoggingEvent.class);
    Mockito.verify(mockedAppender).doAppend(arg.capture());
    LoggingEvent loggingEvent = arg.getValue();
    assertLoggingEvent(loggingEvent,Level.ERROR,false);
  }

  @Override
  protected void assertLoggedAsErrorWithException() {
    ArgumentCaptor<LoggingEvent> arg = ArgumentCaptor.forClass(LoggingEvent.class);
    Mockito.verify(mockedAppender).doAppend(arg.capture());
    LoggingEvent loggingEvent = arg.getValue();
    assertLoggingEvent(loggingEvent,Level.ERROR,true);
  }

  @Override
  protected void configureLoggingFrameworkWithNone() {
    getUnderlyingLogger().setLevel(Level.OFF);
  }

  @Override
  protected void configureLoggingFrameworkWithError() {
    getUnderlyingLogger().setLevel(Level.ERROR);    
  }

}
