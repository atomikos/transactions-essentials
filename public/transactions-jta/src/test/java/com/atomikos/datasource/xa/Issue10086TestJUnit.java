/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa;

import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

public class Issue10086TestJUnit extends TestCase {

	private XidFactory factory;
	
	protected void setUp() throws Exception {
		super.setUp();
		//set counter to max value for max length of branch id
		AbstractXidFactory.counter = new AtomicLong ( Long.MAX_VALUE -1) ;
		factory = new DefaultXidFactory();
	}
	
	//assert that at least 45 characters are supported in resname
	public void testResourceNameLength45()
	{

		String bname = "abcdefghijklmnopqrstuvwxyz1234567890123456789";
		factory.createXid ( "abc" , bname );
	}

}
