package com.atomikos.icatch.system;

import junit.framework.TestCase;

public class WaiterTestJUnit extends TestCase {

	private Waiter waiter;
	
	protected void setUp() throws Exception {
		super.setUp();
		waiter = new Waiter();
	}
	
	public void testToken() throws Exception
	{
		waiter.getToken();
		waiter.giveToken();
	}
	
	public void testNumActive() throws Exception
	{
		assertEquals ( 0 , waiter.getNumActive() );
		waiter.incActives();
		assertEquals ( 1 , waiter.getNumActive() );
		waiter.decActives();
		assertEquals ( 0 , waiter.getNumActive() );
	}
	
	public void testAbortCount() 
	{
		assertEquals ( 0 , waiter.getAbortCount() );
		waiter.incAbortCount();
		assertEquals ( 1 , waiter.getAbortCount() );
	}

}
