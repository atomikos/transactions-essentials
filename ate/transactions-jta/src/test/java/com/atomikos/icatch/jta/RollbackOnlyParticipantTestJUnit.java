package com.atomikos.icatch.jta;

import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.SysException;

import junit.framework.TestCase;

public class RollbackOnlyParticipantTestJUnit extends TestCase {

	private RollbackOnlyParticipant p;
	
	protected void setUp() throws Exception {
		super.setUp();
		p = new RollbackOnlyParticipant ( new StringHeuristicMessage("bla") );
	}
	
	public void testPrepare() throws SysException, HeurHazardException, HeurMixedException {
		try {
			p.prepare();
			fail ( "prepare works" );
		}
		catch ( RollbackException ok ) {}
	}
	
	public void testCommit() throws Exception
	{
		try {
			p.commit ( true );
			fail ( "commit works" );
		}
		catch ( RollbackException ok ) {}
	}
	
	public void testRollback() throws Exception
	{
		p.rollback();
	}
	
	public void testRecover() throws Exception
	{
		assertTrue ( p.recover() );
	}
	
	public void testUri() {
		assertNull ( p.getURI() );
	}
	
	public void testHeuristicMessages() 
	{
		assertNotNull ( p.getHeuristicMessages() );
	}
	
	public void testForget() 
	{
		p.forget();
	}

	public void testSiblingCount ()
	{
		p.setGlobalSiblingCount ( 1 );
	}
	
	public void testCascadeList() 
	{
		p.setCascadeList(null);
	}
}
