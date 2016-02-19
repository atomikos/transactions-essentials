/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
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
