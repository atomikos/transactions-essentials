/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.SysException;

import junit.framework.TestCase;

public class SysExceptionTestJUnit extends TestCase 
{

	public void testSysExceptionWithoutNestedErrors()
	{
		SysException se = new SysException ( null );
		StackTraceElement[] st = se.getStackTrace();
		assertNotNull ( st );
		assertFalse ( st.length == 0 );
	}

	public void testSysExceptionWithNestedException()
	{
		Exception exception = new Exception (  );
		exception.fillInStackTrace();
		SysException se = new SysException ( null , exception );
		StackTraceElement[] st = se.getStackTrace();
		assertNotNull ( st );
		assertFalse ( st.length == 0 );
	}
	
	public void testSysExceptionWithNestedSysException()
	{
		Exception exception = new SysException ( "test" );
		exception.fillInStackTrace();
		SysException se = new SysException (null , exception );
		StackTraceElement[] st = se.getStackTrace();
		assertNotNull ( st );
		assertFalse ( st.length == 0 );
	}
	
}
