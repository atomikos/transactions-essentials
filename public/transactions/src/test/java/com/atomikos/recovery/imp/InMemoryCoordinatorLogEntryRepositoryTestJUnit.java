package com.atomikos.recovery.imp;

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
	public void testRemove() throws Exception {
		givenCoordinatorLogEntryInRepository();
		whenCoordinatorLogEntryRemovedFromRepository();
		thenCoordinatorLogEntryNotFoundInRepository();
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
	private void thenCommittingParticipantsFoundInRepository() {
		ParticipantLogEntry committingParticipantLogEntry = coordinatorLogEntry.participantDetails[0];
		
		Assert.assertTrue(participantLogEntries.contains(committingParticipantLogEntry));
	}
	
	Collection<ParticipantLogEntry> participantLogEntries;
	
	private void whenFindAllCommittingParticipants() {
		participantLogEntries=sut.findAllCommittingParticipants();
		
	}
	private void givenCommittingCoordinatorLogEntry() {
		coordinatorLogEntry=createCoordinatorLogEntry(TxState.COMMITTING);
		sut.put(TID, coordinatorLogEntry);
	}
	
	private void thenCoordinatorLogEntryNotFoundInRepository() {
		CoordinatorLogEntry actual = sut.get(TID);
		Assert.assertNull(actual);
		
	}
	private void whenCoordinatorLogEntryRemovedFromRepository() {
		sut.remove(TID);
		
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
