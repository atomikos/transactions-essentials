/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.tcc.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;

public class TccParticipantTestJUnit {

	private static final String URI = "bla";
	
	private TccParticipant p;
	private ParticipantAdapter mock;
	
	@Before
	public void setUp() throws Exception {
		mock = Mockito.mock(ParticipantAdapter.class);
		Mockito.when(mock.getUri()).thenReturn(URI);
		long expires = System.currentTimeMillis() + 1000l;
		Mockito.when(mock.getExpires()).thenReturn(expires);
		p = new TccParticipant(mock);
	}

	@Test
	public void testGetUri() {
		assertEquals(URI, p.getURI());
	}
	
	@Test
	public void testRollbackCallsHttpDelete() throws SysException, HeurCommitException, HeurMixedException, HeurHazardException, HeurRollbackException {
		p.rollback();
		Mockito.verify(mock).delete();
		Mockito.verify(mock, Mockito.times(0)).put();
	}
	
	@Test
	public void testCommitCallsHttpPut() throws SysException, HeurRollbackException, HeurHazardException, HeurMixedException, RollbackException {
		p.commit(false);
		Mockito.verify(mock).put();
		Mockito.verify(mock, Mockito.times(0)).delete();
	}
	
	@Test
	public void testPrepareReturnsNotReadOnly() throws SysException, RollbackException, HeurHazardException, HeurMixedException {
		assertFalse(p.prepare()==Participant.READ_ONLY);
	}
	
	@Test
	public void testPrepareCallsHttpOptions() throws SysException, RollbackException, HeurHazardException, HeurMixedException {
		p.prepare();
		Mockito.verify(mock).options();
	}
	
	@Test(expected=RollbackException.class)
	public void testPrepareThrowsNearExpiry() throws SysException, RollbackException, HeurHazardException, HeurMixedException {
		Mockito.when(mock.getExpires()).thenReturn(0l);
		p.prepare();
	}
	
	@Test(expected=HeurRollbackException.class) 
	public void testCommitThrowsWhenAlreadyCanceled() throws SysException, HeurRollbackException, HeurHazardException, HeurMixedException, RollbackException {
		Mockito.doThrow(new HeurRollbackException()).when(mock).put();
		p.commit(false);
	}
	
}
