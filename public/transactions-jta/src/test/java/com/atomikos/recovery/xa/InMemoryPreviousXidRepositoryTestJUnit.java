package com.atomikos.recovery.xa;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atomikos.datasource.xa.XID;

public class InMemoryPreviousXidRepositoryTestJUnit {
	
	private static final long EXPIRATION_DELAY = 10;
	
	private InMemoryPreviousXidRepository sut;
	private List<XID> xidsToStoreForNextScan;
	private XID xid;
	
	@Before
	public void setUp() {
		sut = new InMemoryPreviousXidRepository();
		xidsToStoreForNextScan = new ArrayList<XID>();
		xid = new XID("tid", "branch", "resource");
		xidsToStoreForNextScan.add(xid);
		
	}

	@Test
	public void testRemember() throws InterruptedException {
		long expiration = System.currentTimeMillis() + EXPIRATION_DELAY;
		sut.remember(xidsToStoreForNextScan, expiration);
		List<XID> result = sut.findXidsExpiredAt(expiration + 1);
		assertFalse(result.isEmpty());
		assertEquals(xid, result.get(0));
	}
	
	@Test
	public void testForget() {
		long expiration = System.currentTimeMillis() + EXPIRATION_DELAY;
		sut.remember(xidsToStoreForNextScan, expiration);
		sut.forgetXidsExpiredAt(expiration);
		List<XID> result = sut.findXidsExpiredAt(expiration + 1);
		assertTrue(result.isEmpty());
	}

}
