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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class Log4j2LoggerFactoryTestJUnit extends AbstractLoggerFactoryTest {
	
	private org.apache.logging.log4j.core.Appender mockedAppender = Mockito.mock(org.apache.logging.log4j.core.Appender.class);
	
	public void setUp() {
		LoggerFactory.setLoggerFactoryDelegate(new Log4j2LoggerFactoryDelegate());
		logger = LoggerFactory.createLogger(getClass());
		
		Mockito.when(mockedAppender.getName()).thenReturn("mock");
		Mockito.when(mockedAppender.isStarted()).thenReturn(true);
		
		getUnderlyingLogger().addAppender(mockedAppender);
		
		getUnderlyingLogger().setLevel(Level.INFO);
	}

	public void testAssertLog4j2LoggerCreated() {
		assertTrue(logger instanceof Log4j2Logger);
	}

	public void testLogDebug() {
		configureLoggingFrameworkWithDebug();
		logger.logDebug(MESSAGE);
		assertLoggedAsDebug();
	}

	@Override
	protected void assertLoggedAsDebug() {
		ArgumentCaptor<LogEvent> arg = ArgumentCaptor.forClass(LogEvent.class);
		Mockito.verify(mockedAppender).append(arg.capture());
		//Mockito.verify(mockedAppender).doAppend(arg.capture());
		LogEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent,Level.DEBUG,false);
	}

	@Override
	protected void assertLoggedAsDebugWithException() {
		ArgumentCaptor<LogEvent> arg = ArgumentCaptor.forClass(LogEvent.class);
		Mockito.verify(mockedAppender).append(arg.capture());
		LogEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent,Level.DEBUG,true);
	}

	private void assertLoggingEvent(LogEvent loggingEvent,Level expectedLevel,boolean withThrowable) {
		assertEquals(MESSAGE, loggingEvent.getMessage().getFormattedMessage());
		assertEquals(expectedLevel, loggingEvent.getLevel());
		if(withThrowable){
			assertNotNull(loggingEvent.getThrown());
		} else {
			assertNull(loggingEvent.getThrown());
		}

	}

	@Override
	protected void assertLoggedAsInfo() {
		ArgumentCaptor<LogEvent> arg = ArgumentCaptor.forClass(LogEvent.class);
		Mockito.verify(mockedAppender).append(arg.capture());
		LogEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent,Level.INFO,false);
	}

	@Override
	protected void assertLoggedAsInfoWithException() {
		ArgumentCaptor<LogEvent> arg = ArgumentCaptor.forClass(LogEvent.class);
		Mockito.verify(mockedAppender).append(arg.capture());
		LogEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent,Level.INFO,true);

	}

	@Override
	protected void assertLoggedAsWarning() {
		ArgumentCaptor<LogEvent> arg = ArgumentCaptor.forClass(LogEvent.class);
		Mockito.verify(mockedAppender).append(arg.capture());
		LogEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent,Level.WARN,false);
		}

	@Override
	protected void assertLoggedAsWarningWithException() {
		ArgumentCaptor<LogEvent> arg = ArgumentCaptor.forClass(LogEvent.class);
		Mockito.verify(mockedAppender).append(arg.capture());
		LogEvent loggingEvent = arg.getValue();
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

	private org.apache.logging.log4j.core.Logger getUnderlyingLogger() {
		return (org.apache.logging.log4j.core.Logger) LogManager.getLogger(getClass());
	}

  @Override
  protected void assertLoggedAsError() {
    ArgumentCaptor<LogEvent> arg = ArgumentCaptor.forClass(LogEvent.class);
    Mockito.verify(mockedAppender).append(arg.capture());
    LogEvent loggingEvent = arg.getValue();
    assertLoggingEvent(loggingEvent,Level.ERROR,false);
  }

  @Override
  protected void assertLoggedAsErrorWithException() {
    ArgumentCaptor<LogEvent> arg = ArgumentCaptor.forClass(LogEvent.class);
    Mockito.verify(mockedAppender).append(arg.capture());
    LogEvent loggingEvent = arg.getValue();
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
