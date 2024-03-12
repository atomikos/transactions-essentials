/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.internal;

import java.sql.SQLException;

import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;

import junit.framework.TestCase;

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
	
	public void testPrepareNeverReturnsReadOnlyForReadOnlyInstance() throws Exception {
		p.setReadOnly ( true );
		int result = p.prepare();
		assertFalse ( Participant.READ_ONLY == result );
		assertNull ( c.isCommitted() );
	}
	
	public void testPrepareThrowsForNonReadOnlyInstance() throws Exception {
		p.setReadOnly ( false );
		try {
		    p.prepare();
		    fail("Prepare should fail if not readOnly");
		} catch (RollbackException ok) {}
		assertNull ( c.isCommitted() );
	}
	
	
	public void testEquals() {
	    assertEquals(p,p);
	    AtomikosNonXAParticipant p2 = new AtomikosNonXAParticipant ( c , getName() ); 
	    assertEquals(p,p2);
	}
	
	public void testHashCode() {
        AtomikosNonXAParticipant p2 = new AtomikosNonXAParticipant ( c , getName() ); 
        assertEquals(p.hashCode(),p2.hashCode());
	}

	
    private static class TestJtaAwareNonXaConnection implements NonXaConnectionProxy {

    	private Boolean committed = null;
    	
		public void transactionTerminated ( boolean committed )
				throws SQLException {
			this.committed = new Boolean ( committed );
		}
		
		Boolean isCommitted () {
			return committed;
		}

        @Override
        public boolean isAvailableForReuseByPool() {
            return false;
        }
		
	}
    
}
