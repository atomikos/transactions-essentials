package com.atomikos.persistence;

import junit.framework.TestCase;

public class LogExceptionTestJUnit extends TestCase {
	
	private LogException e;
	
	public void testDefault() {
		e = new LogException();
		assertNull ( e.getCause() );
		assertNull ( e.getMessage() );
	}
	
	public void testWithString() {
		String msg = "bla";
		e = new LogException ( msg );
		assertEquals ( msg , e.getMessage() );
		assertNull ( e.getCause() );
	}
	
	public void testWithCauseAndString() {
		Exception cause = new Exception();
		String msg = "bla";
		e = new LogException ( msg , cause );
		assertEquals ( msg , e.getMessage());
		assertSame ( cause,  e.getCause() );
	}
}
