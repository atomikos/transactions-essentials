package com.atomikos.recovery;

import static org.junit.Assert.*;

import org.junit.Test;

import com.atomikos.recovery.ParticipantLogEntry;

public class ParticipantLogEntryTestJUnit {

	private static final String PART = "part";
	private static final String COORD = "coord";
	private static final long EXPIRES = 0;

	@Test
	public void testXaRecoveryRequiresEqualsIgnoresExpires() {
		ParticipantLogEntry e1 = new ParticipantLogEntry(COORD, PART, EXPIRES);
		ParticipantLogEntry e2 = new ParticipantLogEntry(COORD, PART, EXPIRES + 1);
		assertEquals(e1,e2);
	}

}
