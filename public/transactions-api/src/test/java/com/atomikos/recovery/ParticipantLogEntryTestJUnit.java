package com.atomikos.recovery;

import static org.junit.Assert.*;

import org.junit.Test;

import com.atomikos.icatch.TxState;
import com.atomikos.recovery.ParticipantLogEntry;

public class ParticipantLogEntryTestJUnit {

	private static final String PART = "part";
	private static final String COORD = "coord";
	private static final long EXPIRES = 0;

	@Test
	public void testXaRecoveryRequiresEqualsIgnoresExpires() {
		ParticipantLogEntry e1 = new ParticipantLogEntry(COORD, PART, EXPIRES, "desc", TxState.ABORTING);
		ParticipantLogEntry e2 = new ParticipantLogEntry(COORD, PART, EXPIRES + 1, "desc", TxState.ABORTING);
		assertEquals(e1,e2);
	}
	
	@Test
	public void testXaRecoveryRequiresEqualsIgnoresDescription() {
		ParticipantLogEntry e1 = new ParticipantLogEntry(COORD, PART, EXPIRES, "desc", TxState.ABORTING);
		ParticipantLogEntry e2 = new ParticipantLogEntry(COORD, PART, EXPIRES, "desc1", TxState.ABORTING);
		assertEquals(e1,e2);
	}
	
	@Test
	public void testXaRecoveryRequiresEqualsIgnoresState() {
		ParticipantLogEntry e1 = new ParticipantLogEntry(COORD, PART, EXPIRES, "desc", TxState.ABORTING);
		ParticipantLogEntry e2 = new ParticipantLogEntry(COORD, PART, EXPIRES, "desc", TxState.COMMITTING);
		assertEquals(e1,e2);
	}

	@Test
	public void testToString() throws Exception {
		ParticipantLogEntry e1 = new ParticipantLogEntry(COORD, PART, EXPIRES, "desc", TxState.ABORTING);
		String expected="ParticipantLogEntry [id=coord, uri=part, expires=0, state=ABORTING, description=desc]";
		assertEquals(expected,e1.toString());
	}
}
