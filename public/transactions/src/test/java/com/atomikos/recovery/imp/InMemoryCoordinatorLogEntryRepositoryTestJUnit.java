package com.atomikos.recovery.imp;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.atomikos.icatch.TxState;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.ParticipantLogEntry;

public class InMemoryCoordinatorLogEntryRepositoryTestJUnit {

	private static final String TID = "tid";
	InMemoryCoordinatorLogEntryRepository sut = new InMemoryCoordinatorLogEntryRepository();
	CoordinatorLogEntry coordinatorLogEntry;

	@Before
	public void configure(){
		coordinatorLogEntry=createCoordinatorLogEntry(TxState.IN_DOUBT);
	}
	@Test
	public void testPut() throws Exception {
		givenCoordinatorLogEntryInRepository();
		thenCoordinatorLogEntryCanBeFoundInRepository();
	}

	@Test(expected=IllegalArgumentException.class)
	public void testPutSameCoordinatorLogEntryThrows() throws Exception {
		givenCoordinatorLogEntryInRepository();
		whenCoordinatorLogEntryPutInRepository();
	}
	

	@Test
	public void testFind() throws Exception {
		testPut();
	}
	
	@Test
	public void testFindAllCommittingParticipants() throws Exception {
		givenCommittingCoordinatorLogEntry();
		whenFindAllCommittingParticipants();
		thenCommittingParticipantsFoundInRepository();
	}
	
	@Test
	public void testClose() throws Exception {
		Assert.assertTrue(sut.isClosed());
		sut.init(null);
		Assert.assertFalse(sut.isClosed());
		testPut();
		sut.close();
		Assert.assertTrue(sut.isClosed());
		Assert.assertTrue(sut.getAllCoordinatorLogEntries().isEmpty());
		sut.init(null);
		Assert.assertFalse(sut.isClosed());
	}
	
	
	
	
	private void thenCommittingParticipantsFoundInRepository() {
		ParticipantLogEntry committingParticipantLogEntry = coordinatorLogEntry.participants[0];
		for (CoordinatorLogEntry coordinatorLogEntry : committingParticipantLogEntries) {
			Assert.assertTrue(Arrays.asList(coordinatorLogEntry.participants).contains(committingParticipantLogEntry));	
		}
		
	}
	
	Collection<CoordinatorLogEntry> committingParticipantLogEntries;
	private void whenFindAllCommittingParticipants() {
		committingParticipantLogEntries=sut.findAllCommittingCoordinatorLogEntries();
		
	}
	private void givenCommittingCoordinatorLogEntry() {
		coordinatorLogEntry=createCoordinatorLogEntry(TxState.COMMITTING);
		sut.put(TID, coordinatorLogEntry);
	}
	
	private void whenCoordinatorLogEntryPutInRepository() {
		sut.put(TID, coordinatorLogEntry);
	}
	
	private void thenCoordinatorLogEntryCanBeFoundInRepository() {
		CoordinatorLogEntry actual = sut.get(TID);
		Assert.assertEquals(coordinatorLogEntry, actual);
	}

	private void givenCoordinatorLogEntryInRepository() {
		sut.put(TID, coordinatorLogEntry);
	}

	private CoordinatorLogEntry createCoordinatorLogEntry(TxState state) {
		ParticipantLogEntry[] participantLogEntries = new ParticipantLogEntry[1];

		participantLogEntries[0] = newParticipantLogEntryInState(
				state, false);

		coordinatorLogEntry = new CoordinatorLogEntry(TID,participantLogEntries);

		return coordinatorLogEntry;
	}

	private ParticipantLogEntry newParticipantLogEntryInState(TxState state,
			boolean expired) {
		long expires = Long.MAX_VALUE;
		if (expired) {
			expires = 0;
		}
		return new ParticipantLogEntry("tid", "participantUri", expires,
				"description", state);

	}
}
