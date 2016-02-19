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

package com.atomikos.icatch.imp;

import junit.framework.TestCase;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;

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
