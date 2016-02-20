/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource;

import junit.framework.TestCase;

public class HeuristicExceptionTestJUnit extends TestCase {

	private HeuristicException exception;
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testBasicConstructor() 
	{
		String msg = "bla";
		exception = new HeuristicException ( msg );
		assertEquals ( msg , exception.getMessage() );
	}
	
	

}
