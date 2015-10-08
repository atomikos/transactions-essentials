package com.atomikos.recovery.imp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.icatch.TxState;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.CoordinatorLogEntryRepository;
import com.atomikos.recovery.OltpLogException;
import com.atomikos.recovery.ParticipantLogEntry;

public class LogImpTestJunit {

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
	public void testTransitionFromInDoubtToAbortingIsAllowed() throws Exception {
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryInState(TxState.IN_DOUBT);
		Mockito.when(logRepository.get(coordinatorLogEntry.coordinatorId)).thenReturn(coordinatorLogEntry);
		thenWriteAbortingWorks();
	}
	
	@Test(expected=IllegalStateException.class)
	public void testAbortingWithoutPreviousWriteMustFail() throws Exception {
		thenWriteAbortingFails();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	private void thenWriteAbortingFails() throws OltpLogException {
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryInState(TxState.ABORTING);
		whenWriteInOltp(coordinatorLogEntry);
		
	}

	private void thenWriteAbortingWorks() throws OltpLogException {
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryInState(TxState.ABORTING);
		whenWriteInOltp(coordinatorLogEntry);
	}

	private void whenDeleteInOltp(String tid) throws OltpLogException {
		sut.remove(tid);
		
	}

	private void thenDeleteCalledInRepository(String tid) {
		Mockito.verify(logRepository).remove(tid);
	}

	
	private void thenPutCalledInRepository(CoordinatorLogEntry coordinatorLogEntry) {
		Mockito.verify(logRepository).put(coordinatorLogEntry.coordinatorId,coordinatorLogEntry);
	}

	private void whenWriteInOltp(CoordinatorLogEntry coordinatorLogEntry) throws OltpLogException {
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
