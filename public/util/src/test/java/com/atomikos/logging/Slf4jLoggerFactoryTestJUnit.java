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
	

}
