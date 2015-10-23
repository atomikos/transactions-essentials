package com.atomikos.recovery.imp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.icatch.TxState;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.CoordinatorLogEntryRepository;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.ParticipantLogEntry;

public class OltpLogTestJUnit {

	private LogImp sut;
	private CoordinatorLogEntryRepository logRepository;


	@Before
	public void configure() {
		sut = new LogImp();
		logRepository = Mockito.mock(CoordinatorLogEntryRepository.class);
		sut.setRepository(logRepository);
	}

	@Test
	public void testWriteInOltpLogPutsInRepository() throws Exception {
		CoordinatorLogEntry coordinatorLogEntry = createAnyCoordinatorLogEntry();
		whenWriteInOltp(coordinatorLogEntry);
		thenPutCalledInRepository(coordinatorLogEntry);
	}

	@Test
	public void testRemoveInOltpLogRemovesFromRepository() throws Exception {
		CoordinatorLogEntry coordinatorLogEntry = createAnyCoordinatorLogEntry();
		whenDeleteInOltp(coordinatorLogEntry.coordinatorId);
		thenDeleteCalledInRepository(coordinatorLogEntry.coordinatorId);
	}
	
	@Test
	public void testTransitionFromInDoubtToCommittingIsAllowed() throws Exception {
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryInState(TxState.IN_DOUBT);
		Mockito.when(logRepository.get(coordinatorLogEntry.coordinatorId)).thenReturn(coordinatorLogEntry);
		thenWriteCommittingWorks();
	}
	
	

	@Test
	public void testTransitionFromInDoubtToAbortingIsAllowed() throws Exception {
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryInState(TxState.IN_DOUBT);
		Mockito.when(logRepository.get(coordinatorLogEntry.coordinatorId)).thenReturn(coordinatorLogEntry);
		thenWriteAbortingWorks();
	}
	
	@Test
	public void testTransitionFromNullToInDoubtIsAllowed() throws Exception {
		thenWriteInDoubtWorks();
	}
	
	@Test
	public void testTransitionFromNullToCommittingIsAllowed() throws Exception {
		thenWriteCommittingWorks();
	}
	

	@Test(expected=IllegalStateException.class)
	public void testAbortingWithoutPreviousWriteMustFail() throws Exception {
		thenWriteAbortingFails();
	}
	
	@Test(expected=IllegalStateException.class)
	public void testTransitionFromAbortingToInDoubtMustFail() throws Exception {
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryInState(TxState.ABORTING);
		Mockito.when(logRepository.get(coordinatorLogEntry.coordinatorId)).thenReturn(coordinatorLogEntry);
		thenWriteInDoubtFails();
	}
	
	@Test(expected=IllegalStateException.class)
	public void testTransitionFromCommittingToInDoubtMustFail() throws Exception {
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryInState(TxState.ABORTING);
		Mockito.when(logRepository.get(coordinatorLogEntry.coordinatorId)).thenReturn(coordinatorLogEntry);
		thenWriteCommittingFails();
	}
	

	@Test(expected=IllegalStateException.class)
	public void testTransitionFromAbortingToCommittingMustFail() throws Exception {
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryInState(TxState.ABORTING);
		Mockito.when(logRepository.get(coordinatorLogEntry.coordinatorId)).thenReturn(coordinatorLogEntry);
		thenWriteCommittingFails();
	}
	
	
	@Test(expected=IllegalStateException.class)
	public void testTransitionFromCommittingToAbortingMustFail() throws Exception {
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryInState(TxState.COMMITTING);
		Mockito.when(logRepository.get(coordinatorLogEntry.coordinatorId)).thenReturn(coordinatorLogEntry);
		thenWriteAbortingFails();
	}
	
	
	
	
	
	private void thenWriteInDoubtFails() throws LogWriteException {
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryInState(TxState.IN_DOUBT);
		whenWriteInOltp(coordinatorLogEntry);		
	}
	
	

	private void thenWriteCommittingFails() throws LogWriteException {
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryInState(TxState.COMMITTING);
		whenWriteInOltp(coordinatorLogEntry);
		
	}

	private void thenWriteAbortingFails() throws LogWriteException {
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryInState(TxState.ABORTING);
		whenWriteInOltp(coordinatorLogEntry);
		
	}
	private void thenWriteInDoubtWorks() throws LogWriteException {
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryInState(TxState.IN_DOUBT);
		whenWriteInOltp(coordinatorLogEntry);
		
	}
	private void thenWriteAbortingWorks() throws LogWriteException {
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryInState(TxState.ABORTING);
		whenWriteInOltp(coordinatorLogEntry);
	}
	private void thenWriteCommittingWorks() throws LogWriteException {
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryInState(TxState.COMMITTING);
		whenWriteInOltp(coordinatorLogEntry);
		
	}

	private void whenDeleteInOltp(String tid) throws LogWriteException {
		sut.remove(tid);
	}

	private void thenDeleteCalledInRepository(String tid) {
		Mockito.verify(logRepository).remove(tid);
	}

	
	private void thenPutCalledInRepository(CoordinatorLogEntry coordinatorLogEntry) throws IllegalArgumentException, LogWriteException {
		Mockito.verify(logRepository).put(coordinatorLogEntry.coordinatorId,coordinatorLogEntry);
	}

	private void whenWriteInOltp(CoordinatorLogEntry coordinatorLogEntry) throws LogWriteException {
		sut.write(coordinatorLogEntry);
	}

	
	private CoordinatorLogEntry createAnyCoordinatorLogEntry() {
		return createCoordinatorLogEntryInState(TxState.IN_DOUBT);
	}

	private CoordinatorLogEntry createCoordinatorLogEntryInState(TxState... states) {
		return createCoordinatorLogEntryWithParticipantsInState(states);
	}

	private CoordinatorLogEntry createCoordinatorLogEntryWithParticipantsInState(
			TxState... states) {
		String tid="TID";
		ParticipantLogEntry[] participantDetails = new ParticipantLogEntry[states.length];
		for (int i = 0; i < participantDetails.length; i++) {
			participantDetails[i] = new ParticipantLogEntry(tid, "uri", 0,"description", states[i]);
		}
		return new CoordinatorLogEntry(tid, participantDetails);
	}

}
