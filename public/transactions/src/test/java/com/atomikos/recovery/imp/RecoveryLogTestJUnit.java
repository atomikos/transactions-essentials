package com.atomikos.recovery.imp;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.atomikos.icatch.TxState;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.CoordinatorLogEntryRepository;
import com.atomikos.recovery.ParticipantLogEntry;

public class RecoveryLogTestJUnit {

	private LogImp sut;
	private CoordinatorLogEntryRepository logRepository;
	ParticipantLogEntry participantLogEntry;

	@Before
	public void configure() {
		sut = new LogImp();
		logRepository = Mockito.mock(CoordinatorLogEntryRepository.class);
		sut.setRepository(logRepository);
	}

	@Test
	public void testPresumedAbortingOfUnknownCoordinatorCreatesInDoubtCoordinatorLogEntry() throws Exception {
		givenEmptyRepository();
		try {
			whenPresumedAborting();
		} catch (IllegalStateException ok) {}
		thenRepositoryContainsCoordinatorLogEntry(TxState.IN_DOUBT);
	}
	
	@Test(expected=IllegalStateException.class)
	public void testPresumedAbortingOfUnknownCoordinatorThrowsIllegalStateException() throws Exception {
		givenEmptyRepository();
		whenPresumedAborting();
	}

	
	
	
	
	
	private void givenInDoubtCoordinatorInRepository() {
		participantLogEntry = newParticipantLogEntryInState(TxState.IN_DOUBT);
		ParticipantLogEntry[] participantLogEntries = {participantLogEntry};
		CoordinatorLogEntry coordinatorLogEntry = new CoordinatorLogEntry(participantLogEntry.coordinatorId, participantLogEntries);
		
		Mockito.when(logRepository.get(Mockito.anyString())).thenReturn(coordinatorLogEntry);
		
	}

	private void thenRepositoryContainsCoordinatorLogEntry(TxState state) {
		ArgumentCaptor<CoordinatorLogEntry> captor = ArgumentCaptor.forClass(CoordinatorLogEntry.class);
		Mockito.verify(logRepository, Mockito.times(1)).put(Mockito.eq(participantLogEntry.coordinatorId), captor.capture());
		Assert.assertEquals(state, captor.getValue().getResultingState());
	}

	private void givenEmptyRepository() {}

	private void whenPresumedAborting() {
		participantLogEntry = newParticipantLogEntryInState(TxState.IN_DOUBT);
		sut.presumedAborting(participantLogEntry);
	}

	private ParticipantLogEntry newParticipantLogEntryInState(TxState aborting) {
		return new ParticipantLogEntry("tid", "uri", 0, "description", aborting);

	}
}
