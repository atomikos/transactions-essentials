/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.xa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atomikos.datasource.xa.XID;

public class InMemoryPreviousXidRepositoryTestJUnit {
	
	private static final long EXPIRATION_DELAY = 10;
	
	private InMemoryPreviousXidRepository sut;
	private XID xid;
	
	@Before
	public void setUp() {
		sut = new InMemoryPreviousXidRepository();
		xid = new XID("tid", "branch", "resource");
		
	}

	@Test
	public void testRemember() throws InterruptedException {
		long expiration = System.currentTimeMillis() + EXPIRATION_DELAY;
		sut.remember(xid, expiration);
		List<XID> result = sut.findXidsExpiredAt(expiration + 1);
		assertFalse(result.isEmpty());
		assertEquals(xid, result.get(0));
	}
	
	@Test 
	public void testRememberDoesNotOverwriteExistingContentWithSameExpiration() {
	    long expiration = System.currentTimeMillis() + EXPIRATION_DELAY;
	    sut.remember(xid, expiration);
	    sut.remember(new XID("tid2", "branch2", "resource2"), expiration);
	    List<XID> result = sut.findXidsExpiredAt(expiration + 1);
	    assertFalse(result.isEmpty());
	    assertEquals(2, result.size());
	}
	
	@Test
	public void testForget() {
		long expiration = System.currentTimeMillis() + EXPIRATION_DELAY;
		sut.remember(xid, expiration);
		sut.forgetXidsExpiredAt(expiration);
		List<XID> result = sut.findXidsExpiredAt(expiration + 1);
		assertTrue(result.isEmpty());
	}
	

}
