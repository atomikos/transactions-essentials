/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta.hibernate3;

import org.hibernate.HibernateException;

import junit.framework.TestCase;

public class TransactionManagerLookupTestJUnit extends TestCase {

	private TransactionManagerLookup lookup;
	
	protected void setUp() throws Exception {
		super.setUp();
		lookup = new TransactionManagerLookup();
	}
	
	public void testTransactionManager() throws HibernateException {
		assertNotNull ( lookup.getTransactionManager( null) );
	}
	
	public void testName() throws Exception
	{
		assertNull ( lookup.getUserTransactionName() );
	}

}
