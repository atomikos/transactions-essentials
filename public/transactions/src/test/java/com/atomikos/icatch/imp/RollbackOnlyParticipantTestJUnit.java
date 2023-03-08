/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;

import junit.framework.TestCase;

public class RollbackOnlyParticipantTestJUnit extends TestCase {

	private RollbackOnlyParticipant p;

	
	protected void setUp() throws Exception {
		super.setUp();
		p = new RollbackOnlyParticipant();
	}
	
	public void testURI()
	{
		assertNull ( p.getURI() );
	}
	
	public void testCascadeList()
	{
		p.setCascadeList(null);
	}
	
	public void testGlobalSiblingCount()
	{
		p.setGlobalSiblingCount(0);
	}
	
	public void testPrepare() throws SysException, HeurHazardException, HeurMixedException
	{
		try {
			p.prepare();
			fail ( "Prepare works?" );
		}
		catch ( RollbackException ok ) {}
	}
	
	public void testCommit() throws SysException, HeurRollbackException, HeurHazardException, HeurMixedException
	{
		try {
			p.commit ( true );
			fail ( "Commit works?" );
		}
		catch ( RollbackException ok ) {}
	}

	public void testRollback() throws SysException, HeurCommitException, HeurMixedException, HeurHazardException 
	{
		p.rollback();
	}
	
	public void testForget()
	{
		p.forget();
	}
	
}
