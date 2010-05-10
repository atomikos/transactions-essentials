package com.atomikos.icatch.admin.jmx;

import com.atomikos.icatch.admin.TestAdminTransaction;

public class JmxHeuristicTransactionTestJUnit extends
		AbstractJUnitJmxTransactionTest {

	private JmxHeuristicTransaction tx;
	private TestAdminTransaction adminTx;
	
	protected JmxTransaction onSetup(TestAdminTransaction testAdminTransaction,
			TestMBeanServer server) {
		adminTx = testAdminTransaction;
		tx = new JmxHeuristicTransaction ( adminTx );
		try {
			tx.preRegister ( server , null );
		} catch (Exception e) {
			fail ( "Unexpected error");
		}
		return tx;
	}
	
	public void testCommitted()
	{
		adminTx.setWasCommitted ( false );
		assertFalse ( tx.getCommitted() );
		adminTx.setWasCommitted  ( true );
		assertTrue ( tx.getCommitted() );
		adminTx.setWasCommitted ( false );
		assertFalse ( tx.getCommitted() );
	}
	
	public void testForceForget()
	{
		assertFalse ( adminTx.isForceForgetCalled() );
		tx.forceForget();
		assertTrue ( adminTx.isForceForgetCalled() );
	}

}
