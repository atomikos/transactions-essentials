/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PendingTransactionRecordTestJUnit {

	PendingTransactionRecord given;
	@Test
	public void testToRecord() throws Exception {
		String id = "id1";
		TxState state = TxState.COMMITTING;
		long expires = 100l;
		PendingTransactionRecord given = new PendingTransactionRecord(id, state, expires, "domain");
		//when
		String record = given.toRecord();
		//then
		assertEquals("id1|COMMITTING|100|domain|"+System.lineSeparator(), record);
	}
	@Test
	public void testFromRecord() throws Exception {
		String givenRecord = "id1|COMMITTING|100|domain|123";
		PendingTransactionRecord pendingTransactionRecord = PendingTransactionRecord.fromRecord(givenRecord);
		assertEquals("id1", pendingTransactionRecord.id);
		assertEquals(TxState.COMMITTING, pendingTransactionRecord.state);
		assertEquals(100, pendingTransactionRecord.expires);
		assertEquals("123", pendingTransactionRecord.superiorId);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFromPartialRecordThrows() {
		String givenRecord = "id1|COMMITTING|";
		PendingTransactionRecord.fromRecord(givenRecord);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFromInvalidRecordThrows() {
		String givenRecord = "id1-COMMITTING-";
		PendingTransactionRecord.fromRecord(givenRecord);
	}
	
}
