package com.atomikos.icatch.imp;

import java.util.Stack;

import com.atomikos.icatch.SysException;

import junit.framework.TestCase;

public class SysExceptionTestJUnit extends TestCase 
{

	private static void assertContains ( 
			String msg , StackTraceElement[] st )
	{
		
		boolean found = false;
		if ( st == null || st.length == 0 ) fail ( "No elements");
		for ( int i = 0 ; i < st.length ; i++ ) {
			if ( st[i].toString().indexOf ( msg ) >= 0 ) found = true;
		}
		if ( ! found ) {
			for ( int i = 0 ; i < st.length ; i++ ) {
				System.out.println ( st[i] );
				
			}
			System.out.println();
			fail ( "Not found in stack trace: " + msg );
		}
	}
	
	public void testSysExceptionWithoutNestedErrors()
	{
		final String msg = "com.atomikos.icatch.SysException";
		SysException se = new SysException ( null );
		StackTraceElement[] st = se.getStackTrace();
		assertNotNull ( st );
		assertFalse ( st.length == 0 );
	}

	public void testSysExceptionWithNestedException()
	{
		Exception exception = new Exception (  );
		exception.fillInStackTrace();
		Stack errors = new Stack();
		errors.push ( exception );
		SysException se = new SysException ( null , errors );
		StackTraceElement[] st = se.getStackTrace();
		assertNotNull ( st );
		assertFalse ( st.length == 0 );
	}
	
	public void testSysExceptionWithNestedSysException()
	{
		Exception exception = new SysException ( "test" );
		exception.fillInStackTrace();
		Stack errors = new Stack();
		errors.push ( exception );
		SysException se = new SysException (null , errors );
		StackTraceElement[] st = se.getStackTrace();
		assertNotNull ( st );
		assertFalse ( st.length == 0 );
	}
	
}
