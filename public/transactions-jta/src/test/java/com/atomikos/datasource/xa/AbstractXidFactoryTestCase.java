/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa;

import javax.transaction.xa.Xid;

import junit.framework.TestCase;

/**
 * Common test logic for xid factory.
 * 
 *
 */
public abstract class AbstractXidFactoryTestCase extends TestCase {

	private XidFactory factory;
	
	protected final void setUp() throws Exception {
		super.setUp();
		factory = createXidFactory();
	}
	
	protected XidFactory getFactory()
	{
		return factory;
	}
	
	public void testBranchesAreDifferentForSameTid() {
		String tid = "mytid";
		String name = "name";
		Xid xid = factory.createXid ( tid , name , name );
		Xid xid2 = factory.createXid ( tid , name , name);
		String bqual = new String ( xid.getBranchQualifier());
		String bqual2 = new String ( xid2.getBranchQualifier());
		assertFalse ( bqual.equals ( bqual2 ) );
	}
	
	public void testGtidsAreSameForSameTid() {
		String tid = "mytid";
		String name = "name";
		Xid xid = factory.createXid ( tid , name, name );
		Xid xid2 = factory.createXid ( tid , name, name );
		String gtid = new String ( xid.getGlobalTransactionId() );
		String gtid2 = new String ( xid2.getGlobalTransactionId() );
		assertEquals ( gtid , gtid2 );
	}
	
	public void testGtidsAreDifferentForDifferentTid() {
		String tid = "mytid";
		String tid2 = "mytid2";
		String name = "name";
		Xid xid = factory.createXid ( tid , name, name );
		Xid xid2 = factory.createXid ( tid2 , name, name);
		String gtid = new String ( xid.getGlobalTransactionId() );
		String gtid2 = new String ( xid2.getGlobalTransactionId() );
		assertFalse ( gtid.equals ( gtid2 ) );
	}
	
	public void testUniqueResourceName() {
		String tid = "mytid";
		String tid2 = "mytid2";
		String name = "name";
		String resName = "resName";
		XID xid = factory.createXid ( tid , name, resName );
		assertEquals(resName, xid.getUniqueResourceName());
	}

	protected abstract XidFactory createXidFactory();

}
