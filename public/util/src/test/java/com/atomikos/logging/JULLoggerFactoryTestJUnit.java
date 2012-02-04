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
		assertEquals(MESSAGE, logRecord.getMessage());
		assertEquals(Level.FINE, logRecord.getLevel());
	}



	@Override
	protected void assertLoggedAsDebugWithException() {
		ArgumentCaptor<LogRecord> arg = ArgumentCaptor.forClass(LogRecord.class);
		Mockito.verify(handler).publish(arg.capture());
		LogRecord logRecord = arg.getValue();

		assertEquals(MESSAGE, logRecord.getMessage());
		assertEquals(Level.FINE, logRecord.getLevel());
		assertNotNull(logRecord.getThrown());
	}

	@Override
	protected void assertLoggedAsInfo() {
		ArgumentCaptor<LogRecord> arg = ArgumentCaptor.forClass(LogRecord.class);
		Mockito.verify(handler).publish(arg.capture());
		LogRecord logRecord = arg.getValue();
		assertEquals(MESSAGE, logRecord.getMessage());
		assertEquals(Level.INFO, logRecord.getLevel());
	}

	@Override
	protected void assertLoggedAsInfoWithException() {
		ArgumentCaptor<LogRecord> arg = ArgumentCaptor.forClass(LogRecord.class);
		Mockito.verify(handler).publish(arg.capture());
		LogRecord logRecord = arg.getValue();
		assertEquals(MESSAGE, logRecord.getMessage());
		assertEquals(Level.INFO, logRecord.getLevel());
		assertNotNull(logRecord.getThrown());
	}

	@Override
	protected void assertLoggedAsWarning() {
		ArgumentCaptor<LogRecord> arg = ArgumentCaptor.forClass(LogRecord.class);
		Mockito.verify(handler).publish(arg.capture());
		LogRecord logRecord = arg.getValue();
		assertEquals(MESSAGE, logRecord.getMessage());
		assertEquals(Level.WARNING, logRecord.getLevel());
	}

	@Override
	protected void assertLoggedAsWarningWithException() {
		ArgumentCaptor<LogRecord> arg = ArgumentCaptor.forClass(LogRecord.class);
		Mockito.verify(handler).publish(arg.capture());
		LogRecord logRecord = arg.getValue();
		assertEquals(MESSAGE, logRecord.getMessage());
		assertEquals(Level.WARNING, logRecord.getLevel());
		assertNotNull(logRecord.getThrown());
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

}
