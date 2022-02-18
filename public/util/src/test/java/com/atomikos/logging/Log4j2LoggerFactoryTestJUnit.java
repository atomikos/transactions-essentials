/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class Log4j2LoggerFactoryTestJUnit extends AbstractLoggerFactoryTest {

	private org.apache.logging.log4j.core.Appender mockedAppender = Mockito
			.mock(org.apache.logging.log4j.core.Appender.class);

	public void setUp() {
		LoggerFactory
				.setLoggerFactoryDelegate(new Log4j2LoggerFactoryDelegate());
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
		// Mockito.verify(mockedAppender).doAppend(arg.capture());
		LogEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent, Level.DEBUG, false);
	}

	@Override
	protected void assertLoggedAsDebugWithException() {
		ArgumentCaptor<LogEvent> arg = ArgumentCaptor.forClass(LogEvent.class);
		Mockito.verify(mockedAppender).append(arg.capture());
		LogEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent, Level.DEBUG, true);
	}

	private void assertLoggingEvent(LogEvent loggingEvent, Level expectedLevel,
			boolean withThrowable) {
		assertEquals(MESSAGE, loggingEvent.getMessage().getFormattedMessage());
		assertEquals(expectedLevel, loggingEvent.getLevel());
		if (withThrowable) {
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
		assertLoggingEvent(loggingEvent, Level.INFO, false);
	}

	@Override
	protected void assertLoggedAsInfoWithException() {
		ArgumentCaptor<LogEvent> arg = ArgumentCaptor.forClass(LogEvent.class);
		Mockito.verify(mockedAppender).append(arg.capture());
		LogEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent, Level.INFO, true);

	}

	@Override
	protected void assertLoggedAsWarning() {
		ArgumentCaptor<LogEvent> arg = ArgumentCaptor.forClass(LogEvent.class);
		Mockito.verify(mockedAppender).append(arg.capture());
		LogEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent, Level.WARN, false);
	}

	@Override
	protected void assertLoggedAsWarningWithException() {
		ArgumentCaptor<LogEvent> arg = ArgumentCaptor.forClass(LogEvent.class);
		Mockito.verify(mockedAppender).append(arg.capture());
		LogEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent, Level.WARN, true);
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
		return (org.apache.logging.log4j.core.Logger) LogManager
				.getLogger(getClass());
	}

	@Override
	protected void assertLoggedAsError() {
		ArgumentCaptor<LogEvent> arg = ArgumentCaptor.forClass(LogEvent.class);
		Mockito.verify(mockedAppender).append(arg.capture());
		LogEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent, Level.ERROR, false);
	}

	@Override
	protected void assertLoggedAsErrorWithException() {
		ArgumentCaptor<LogEvent> arg = ArgumentCaptor.forClass(LogEvent.class);
		Mockito.verify(mockedAppender).append(arg.capture());
		LogEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent, Level.ERROR, true);
	}

	@Override
	protected void configureLoggingFrameworkWithNone() {
		getUnderlyingLogger().setLevel(Level.OFF);
	}

	@Override
	protected void configureLoggingFrameworkWithError() {
		getUnderlyingLogger().setLevel(Level.ERROR);
	}

	@Override
	protected void configureLoggingFrameworkWithTrace() {
		getUnderlyingLogger().setLevel(Level.TRACE);

	}

	@Override
	protected void assertLoggedAsTrace() {
		ArgumentCaptor<LogEvent> arg = ArgumentCaptor.forClass(LogEvent.class);
		Mockito.verify(mockedAppender).append(arg.capture());
		LogEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent, Level.TRACE, false);

	}

	@Override
	protected void assertLoggedAsTraceWithException() {
		ArgumentCaptor<LogEvent> arg = ArgumentCaptor.forClass(LogEvent.class);
		Mockito.verify(mockedAppender).append(arg.capture());
		LogEvent loggingEvent = arg.getValue();
		assertLoggingEvent(loggingEvent, Level.TRACE, true);

	}

}
