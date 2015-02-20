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
		Xid xid = factory.createXid ( tid , name );
		Xid xid2 = factory.createXid ( tid , name );
		String bqual = new String ( xid.getBranchQualifier());
		String bqual2 = new String ( xid2.getBranchQualifier());
		assertFalse ( bqual.equals ( bqual2 ) );
	}
	
	public void testGtidsAreSameForSameTid() {
		String tid = "mytid";
		String name = "name";
		Xid xid = factory.createXid ( tid , name );
		Xid xid2 = factory.createXid ( tid , name );
		String gtid = new String ( xid.getGlobalTransactionId() );
		String gtid2 = new String ( xid2.getGlobalTransactionId() );
		assertEquals ( gtid , gtid2 );
	}
	
	public void testGtidsAreDifferentForDifferentTid() {
		String tid = "mytid";
		String tid2 = "mytid2";
		String name = "name";
		Xid xid = factory.createXid ( tid , name );
		Xid xid2 = factory.createXid ( tid2 , name );
		String gtid = new String ( xid.getGlobalTransactionId() );
		String gtid2 = new String ( xid2.getGlobalTransactionId() );
		assertFalse ( gtid.equals ( gtid2 ) );
	}

	protected abstract XidFactory createXidFactory();

}
