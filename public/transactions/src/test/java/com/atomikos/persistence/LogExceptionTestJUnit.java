package com.atomikos.persistence;

import junit.framework.TestCase;

public class LogExceptionTestJUnit extends TestCase {
	
	private LogException e;
	
	public void testDefault() {
		e = new LogException();
		assertNull ( e.getErrors() );
		assertNull ( e.getMessage() );
	}
	
	public void testWithString() {
		String msg = "bla";
		e = new LogException ( msg );
		assertEquals ( msg , e.getMessage() );
		assertNull ( e.getErrors() );
	}
	
	public void testWithStack() {
		java.util.Stack s = new java.util.Stack();
		e = new LogException ( s );
		assertNull ( e.getMessage() );
		assertNotNull ( e.getErrors() );
	}
	
	public void testWithStackAndString() {
		java.util.Stack s = new java.util.Stack();
		String msg = "bla";
		e = new LogException ( msg , s );
		assertEquals ( msg , e.getMessage());
		assertNotNull ( e.getErrors() );
	}
	
	
}
