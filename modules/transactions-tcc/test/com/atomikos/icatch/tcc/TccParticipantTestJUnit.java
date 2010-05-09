package com.atomikos.icatch.tcc;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;

public class TccParticipantTestJUnit extends TestCase 
{

	private TccParticipant part;
	private String id;
	private long timeout;
	private TestTccService service;
	
	public TccParticipantTestJUnit ( String name )
	{
		super ( name );
	}
	
	protected void setUp()
	{
		id = "id" + System.currentTimeMillis();
		timeout = 1000;
		service = new TestTccService();
		UserTccServiceManager.doRegisterForRecovery ( service );
		part = new TccParticipant ( service , id , timeout );
	}
	
	public void tearDown()
	{
		UserTccServiceManager.services = new ArrayList();
	}
	
	public void testGetURI()
	{
		assertEquals ( part.getURI() , id );
	}
	
	public void testCompleted()
	{
		assertFalse ( part.isCompleted() );
		part.setCompleted();
		assertTrue ( part.isCompleted() );
	}
	
	public void testHeuristicMessage()
	{
		String msg = part.getHeuristicMessages()[0].toString();
		assertTrue ( msg.indexOf ( id ) >= 0 );
	}
	
	public void testPrepareFailsBeforeCompletion() throws SysException, RollbackException, HeurHazardException, HeurMixedException
	{
		try {
			part.prepare();
			fail ( "Prepare works if not completed" );
		}
		catch ( RollbackException ok ) {}
		
	}
	
	public void testPrepareWorksAfterCompleted()
	throws Exception
	{
		part.setCompleted();
		assertFalse ( part.prepare() == Participant.READ_ONLY );
	}
	
	public void testRecoverWorksInNormalOperation()
	{
		assertFalse ( service.isRecovered(id));
		assertTrue ( part.recover() );
		assertFalse ( service.isRecovered (id ));
		
	}

	public void testRecoverWorksAfterDeserialization()
	{
		
		
		part = new TccParticipant ( null , id , timeout );
		assertTrue ( part.recover() );
		assertTrue ( part.isCompleted() );
		assertTrue ( service.isRecovered(id));
	}
	
	public void testRecoverFailsWithoutApplication()
	{
		part = new TccParticipant ( null , id , timeout );
		UserTccServiceManager.services = new ArrayList();
		assertFalse ( part.recover() );
		//completed must be false if not recovered
		assertFalse ( part.isCompleted() );
		assertFalse ( service.isRecovered(id));
	}
	
	public void testRollbackFailsIfNotCompleted()
	throws Exception
	{
		try {
			part.rollback();
			fail ( "Rollback works before complete" );
		}
		catch ( HeurHazardException ok ) {}
		assertFalse ( service.isCanceled ( id ) );
		assertFalse ( service.isConfirmed ( id ) );
	}
	
	public void testRollbackWorksAfterCompleted()
	throws Exception
	{
		assertFalse ( service.isCanceled ( id ) );
		part.setCompleted();
		part.rollback();
		assertTrue ( service.isCanceled (id ) );
		assertFalse ( service.isConfirmed ( id ) );
	}
	
	public void testCommitOnePhase()
	throws Exception
	{
		assertFalse ( service.isConfirmed(id));
		part.setCompleted();
		part.commit ( true );
		assertTrue ( service.isConfirmed (id));
		assertFalse ( service.isCanceled(id));
	}
	
	public void testCommitTwoPhase()
	throws Exception
	{
		assertFalse ( service.isConfirmed(id));
		part.setCompleted();
		part.commit ( false );
		assertTrue ( service.isConfirmed (id));
		assertFalse ( service.isCanceled(id));
	}
	
	public void testCommitFailsWithoutApplication()
	throws Exception
	{
		part = new TccParticipant ( null , id , timeout );
		UserTccServiceManager.services = new ArrayList();
		try {
			part.commit ( true );
			fail ( "Commit works without application");
		}
		catch ( HeurHazardException ok ){}
		assertFalse ( service.isConfirmed(id));
	}
	
	public void testCommitFailsIfNotCompleted()
	throws Exception
	{
		try {
			part.commit( true );
			fail ( "Commit works before complete" );
		}
		catch ( HeurHazardException ok ) {}
		assertFalse ( service.isCanceled ( id ) );
		assertFalse ( service.isConfirmed ( id ) );
	}
	
}
