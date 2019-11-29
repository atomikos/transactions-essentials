/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta.hibernate3;

import junit.framework.TestCase;

public class AtomikosJTATransactionFactoryTestJUnit extends TestCase 
{

	private AtomikosJTATransactionFactory hibernate;
	
	protected void setUp() throws Exception 
	{
		super.setUp();
		hibernate = new AtomikosJTATransactionFactory();
	}
	
	public void testIssue58114() 
	{
		try {
			hibernate.configure ( null );
		} catch ( NullPointerException bug58114 ) {
			fail ( "Calling configure should not throw exceptions or Hibernate-JTA will not work!" );
		}
		
		testUserTransaction();
	}

	public void testUserTransaction() 
	{
		assertNotNull ( hibernate.getUserTransaction() );
	}
}
