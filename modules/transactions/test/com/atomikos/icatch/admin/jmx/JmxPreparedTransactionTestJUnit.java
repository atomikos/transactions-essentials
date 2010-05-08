package com.atomikos.icatch.admin.jmx;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.admin.TestAdminTransaction;

public class JmxPreparedTransactionTestJUnit extends
		AbstractJUnitJmxTransactionTest {

	private JmxPreparedTransaction tx;
	private TestAdminTransaction adminTx;
	
	protected JmxTransaction onSetup(TestAdminTransaction testAdminTransaction,
			TestMBeanServer server) {
		adminTx = testAdminTransaction;
		tx = new JmxPreparedTransaction ( testAdminTransaction );
		try {
			tx.preRegister ( server , null );
		} catch (Exception e) {
			fail ( "Unexpected error" );
		}
		return tx;
	}
	
	public void testForceCommit() throws SysException, HeurRollbackException, HeurHazardException, HeurMixedException
	{
		assertFalse ( adminTx.isForceCommitCalled() );
		tx.forceCommit();
		assertTrue ( adminTx.isForceCommitCalled() );
	}
	
	public void testForceRollback() throws SysException, HeurCommitException, HeurHazardException, HeurMixedException
	{
		assertFalse ( adminTx.isForceRollbackCalled() );
		tx.forceRollback();
		assertTrue ( adminTx.isForceRollbackCalled() );
	}

}
