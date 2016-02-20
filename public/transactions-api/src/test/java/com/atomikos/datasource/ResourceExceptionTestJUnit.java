/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

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
	
}
