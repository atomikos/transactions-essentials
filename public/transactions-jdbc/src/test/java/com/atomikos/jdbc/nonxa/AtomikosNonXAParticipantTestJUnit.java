/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.nonxa;

import java.sql.SQLException;

import junit.framework.TestCase;

import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;

public class AtomikosNonXAParticipantTestJUnit extends TestCase 
{
	AtomikosNonXAParticipant p;
	TestJtaAwareNonXaConnection c;

	protected void setUp() throws Exception 
	{
		super.setUp();
		c = new TestJtaAwareNonXaConnection();
		p = new AtomikosNonXAParticipant ( c , getName() );	
	}
	
	private AtomikosNonXAParticipant createRecoveredInstance() 
	{
		return new AtomikosNonXAParticipant ( null , getName() );
	}

	public void testPrepareNeverReturnsReadOnlyForReadOnlyInstance() throws Exception {
		p.setReadOnly ( true );
		int result = p.prepare();
		assertFalse ( Participant.READ_ONLY == result );
		assertNull ( c.isCommitted() );
	}
	
	public void testPrepareReturnsNotReadOnlyForNonReadOnlyInstance() throws Exception {
		p.setReadOnly ( false );
		int result = p.prepare();
		assertFalse ( Participant.READ_ONLY ==  result );
		assertNull ( c.isCommitted() );
	}
	
	public void testCommitFailsForNonReadOnlyRecoveredInstance() throws Exception {
		p = createRecoveredInstance();
		p.setReadOnly(false);
		try {
			p.commit ( false );
			fail ( "commit works without a connection after recovery?" );
		} catch ( HeurRollbackException ok ) {
			
		}
	}
	
	public void testCommitWorksForReadOnlyRecoveredInstance() throws Exception 
	{
		p = createRecoveredInstance();
		p.setReadOnly(true);
		p.commit(false);
	}
	
	public void testRollbackWorksForRecoveredInstance() throws Exception {
		p = createRecoveredInstance();
		p.rollback();
	}
	
	public void testTwoPhaseCommitWorksAfterIntermediateRecoveryScan() throws Exception {
		p.recover();
		p.setReadOnly(false);
		p.prepare();
		p.commit(false);
	}
	
	public void testOnePhaseCommitWorksAfterIntermediateRecoveryScan() throws Exception {
		p.recover();
		p.setReadOnly(false);
		p.commit(true);
	}
	
	public void testRollbackWorksAfterIntermediateRecoveryScan() throws Exception
	{
		p.recover();
		p.setReadOnly(false);
		p.rollback();
	}
	
    private static class TestJtaAwareNonXaConnection implements JtaAwareNonXaConnection {

    	private Boolean committed = null;
    	
		public void transactionTerminated ( boolean committed )
				throws SQLException {
			this.committed = new Boolean ( committed );
		}
		
		Boolean isCommitted () {
			return committed;
		}
		
	}
    
}
