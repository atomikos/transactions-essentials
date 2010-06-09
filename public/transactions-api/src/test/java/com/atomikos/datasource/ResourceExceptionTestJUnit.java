package com.atomikos.datasource;

import junit.framework.TestCase;

public class ResourceExceptionTestJUnit extends TestCase 
{
	private ResourceException exception;
	
	public void testBasicConstructor() {
		String msg = "bla";
		exception = new ResourceException ( msg );
		assertEquals ( msg , exception.getMessage() );
	}
	
	public void testConstructorWithMessageAndDetails() {
		String msg = "bla";
		java.util.Stack details = new java.util.Stack();
		exception = new ResourceException ( msg , details );
		assertEquals ( msg , exception.getMessage() );
		assertEquals ( details , exception.getDetails() );
	}
	
	public void testConstructorDetails() {
		java.util.Stack details = new java.util.Stack();
		exception = new ResourceException ( details );
		assertEquals ( details , exception.getDetails() );
		
	}
}
