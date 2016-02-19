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

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class JULLoggerFactoryTestJUnit extends AbstractLoggerFactoryTest {

	//can't mock the "logger" Itself but Logger call a "publish" method on a mocked handler
	private Handler handler = Mockito.mock(Handler.class);

	@Override
	public void setUp() {
		LoggerFactory.setLoggerFactoryDelegate(new JULLoggerFactoryDelegate());
		logger = LoggerFactory.createLogger(getClass());
		getUnderlyingLogger().addHandler(handler);
		getUnderlyingLogger().setLevel(Level.INFO);
	}

	public void testAssertSlf4jLoggerCreated() {
		assertTrue(logger instanceof JULLogger);
	}


	@Override
	protected void assertLoggedAsDebug() {
		ArgumentCaptor<LogRecord> arg = ArgumentCaptor.forClass(LogRecord.class);
		Mockito.verify(handler).publish(arg.capture());
		LogRecord logRecord = arg.getValue();
		assertLogRecord(logRecord,Level.FINE,false);
	}



	@Override
	protected void assertLoggedAsDebugWithException() {
		ArgumentCaptor<LogRecord> arg = ArgumentCaptor.forClass(LogRecord.class);
		Mockito.verify(handler).publish(arg.capture());
		LogRecord logRecord = arg.getValue();

		assertLogRecord(logRecord,Level.FINE,true);
	}

	@Override
	protected void assertLoggedAsInfo() {
		ArgumentCaptor<LogRecord> arg = ArgumentCaptor.forClass(LogRecord.class);
		Mockito.verify(handler).publish(arg.capture());
		LogRecord logRecord = arg.getValue();
		assertLogRecord(logRecord,Level.INFO,false);
	}

	@Override
	protected void assertLoggedAsInfoWithException() {
		ArgumentCaptor<LogRecord> arg = ArgumentCaptor.forClass(LogRecord.class);
		Mockito.verify(handler).publish(arg.capture());
		LogRecord logRecord = arg.getValue();
		assertLogRecord(logRecord,Level.INFO,true);
	}

	@Override
	protected void assertLoggedAsWarning() {
		ArgumentCaptor<LogRecord> arg = ArgumentCaptor.forClass(LogRecord.class);
		Mockito.verify(handler).publish(arg.capture());
		LogRecord logRecord = arg.getValue();
		assertLogRecord(logRecord,Level.WARNING,false);
	}

	@Override
	protected void assertLoggedAsWarningWithException() {
		ArgumentCaptor<LogRecord> arg = ArgumentCaptor.forClass(LogRecord.class);
		Mockito.verify(handler).publish(arg.capture());
		LogRecord logRecord = arg.getValue();
		assertLogRecord(logRecord,Level.WARNING,true);

	}

	private void assertLogRecord(LogRecord logRecord, Level expectedLevel,boolean withThrowable) {
		assertEquals(MESSAGE, logRecord.getMessage());
		assertEquals(expectedLevel, logRecord.getLevel());
		if(withThrowable){
			assertNotNull(logRecord.getThrown());
		} else {
			assertNull(logRecord.getThrown());
		}
	}

	public void testIsInfoEnabled() {
		getUnderlyingLogger().setLevel(Level.WARNING);
		super.testIsInfoEnabled();
	}

	@Override
	protected void configureLoggingFrameworkWithInfo() {
		getUnderlyingLogger().setLevel(Level.INFO);
	}

	@Override
	protected void configureLoggingFrameworkWithDebug() {
		getUnderlyingLogger().setLevel(Level.FINE);
	}

	private java.util.logging.Logger getUnderlyingLogger() {
		return java.util.logging.Logger.getLogger(getClass().getName());
	}

  @Override
  protected void assertLoggedAsError() {
    ArgumentCaptor<LogRecord> arg = ArgumentCaptor.forClass(LogRecord.class);
    Mockito.verify(handler).publish(arg.capture());
    LogRecord logRecord = arg.getValue();
    assertLogRecord(logRecord,Level.SEVERE,false);
  }

  @Override
  protected void assertLoggedAsErrorWithException() {
    ArgumentCaptor<LogRecord> arg = ArgumentCaptor.forClass(LogRecord.class);
    Mockito.verify(handler).publish(arg.capture());
    LogRecord logRecord = arg.getValue();
    assertLogRecord(logRecord,Level.SEVERE,true);
  }

  @Override
  protected void configureLoggingFrameworkWithError() {
    getUnderlyingLogger().setLevel(Level.SEVERE);    
  }

  @Override
  protected void configureLoggingFrameworkWithNone() {
    getUnderlyingLogger().setLevel(Level.OFF);
  }

}
