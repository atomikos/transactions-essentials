package com.atomikos.recovery.imp;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.atomikos.icatch.TxState;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.CoordinatorLogEntryRepository;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.ParticipantLogEntry;
import com.atomikos.util.UniqueIdMgr;

public class RecoveryLogTestJUnit {

	private static final boolean EXPIRED = true;
	private static final boolean NON_EXPIRED = false;
	private LogImp sut;
	private CoordinatorLogEntryRepository logRepository;
	ParticipantLogEntry participantLogEntry;
	
	Collection<ParticipantLogEntry> committingParticipants;

	private UniqueIdMgr uniqueIdMgr = new UniqueIdMgr(getClass().getName());

	@Before
	public void configure() {
		sut = new LogImp();
		logRepository = Mockito.mock(CoordinatorLogEntryRepository.class);
		sut.setRepository(logRepository);
	}

	@Test
	public void testPresumedAbortingOfUnknownCoordinatorCreatesInDoubtCoordinatorLogEntry()
			throws Exception {
		givenEmptyRepository();
		try {
			whenPresumedAborting();
		} catch (IllegalStateException ok) {
		}
		thenRepositoryContainsCoordinatorLogEntry(TxState.IN_DOUBT);
	}

	@Test(expected = IllegalStateException.class)
	public void testPresumedAbortingOfUnknownCoordinatorThrowsIllegalStateException()
			throws Exception {
		givenEmptyRepository();
		whenPresumedAborting();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testPresumedAbortingThrowsForNullEntry() throws IllegalStateException, LogException {
		sut.presumedAborting(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPresumedAbortingThrowsIfNotIndoubt() throws IllegalStateException, LogException {
		participantLogEntry = newParticipantLogEntryInState(TxState.ABORTING, NON_EXPIRED);
		sut.presumedAborting(participantLogEntry);
	}
	
	@Test
	public void testPresumedAbortingOfExpiredInDoubtCoordinatorCreatesAbortingCoordinatorLogEntry()
			throws Exception {
		givenCoordinatorInRepository(TxState.IN_DOUBT, EXPIRED);
		whenPresumedAborting();
		thenRepositoryContainsCoordinatorLogEntry(TxState.ABORTING);
	}

	@Test
	public void testPresumedAbortingOfExpiredCommittingCoordinatorDoesNotUpdateLog()
			throws Exception {
		givenCoordinatorInRepository(TxState.COMMITTING, EXPIRED);
		try {
			whenPresumedAborting();
		} catch (IllegalStateException ok) {
		}

		thenPutWasNotCalledOnRepository();
	}

	@Test
	public void testTerminatedOfParticipantForExistingCoordinatorUpdatesLog()
			throws Exception {
		givenCoordinatorInRepository(TxState.COMMITTING, NON_EXPIRED, TxState.COMMITTING);
		whenTerminated();
		thenRepositoryWasUpdated();

	}

	
	
	@Test
	public void testHeuristicCommitOfParticipantForExistingCoordinatorUpdatesLog() throws Exception {
		givenCoordinatorInRepository(TxState.HEUR_COMMITTED, NON_EXPIRED, TxState.COMMITTING);
		whenTerminatedWithHeuristicCommit();
		thenRepositoryWasUpdated();
	}
	
	
	
	@Test
	public void testHeuristicMixedOfExistingCoordinatorNotResultingInHeurMixedUpdatesLog() throws Exception {
		givenCoordinatorInRepository(TxState.HEUR_MIXED, NON_EXPIRED, TxState.COMMITTING);
		whenTerminatedWithHeuristicMixed();
		thenRepositoryWasUpdated();
	}

	
	@Test
	public void testHeuristicRollbackOfExistingCoordinatorCommittingRemovesFromLog() throws Exception {
		givenCoordinatorInRepository(TxState.COMMITTING, NON_EXPIRED,TxState.COMMITTING);
		whenTerminatedWithHeuristicRollback();
		thenRepositoryWasUpdated();
	}
	
	
	@Test
	public void testExistingCommittingParticipantsMustBefound() throws Exception {
		givenCoordinatorInRepository(TxState.COMMITTING, NON_EXPIRED);
		whenGetCommittingParticipants();
		thenCommittingParticipantsAreFoundInRepository();
	}
	
	@Test
	public void testNonExistingCommittingParticipantsMustBefound() throws Exception {
		givenCoordinatorInRepository(TxState.IN_DOUBT, NON_EXPIRED);
		whenGetCommittingParticipants();
		thenCommittingParticipantsAreNotFoundInRepository();
	}

	private void thenCommittingParticipantsAreNotFoundInRepository() {
		Assert.assertFalse( committingParticipants.contains(participantLogEntry));		
	}

	private void thenCommittingParticipantsAreFoundInRepository() {
		Assert.assertTrue( committingParticipants.contains(participantLogEntry));
	}

	private Collection<ParticipantLogEntry> whenGetCommittingParticipants()
			throws LogReadException {
		committingParticipants = sut.getCommittingParticipants();
		return committingParticipants;
	}
	
	
	private void whenTerminatedWithHeuristicRollback() throws LogException {
		sut.terminatedWithHeuristicRollback(participantLogEntry);
	}

	private void whenTerminatedWithHeuristicMixed() throws LogException {
		sut.terminatedWithHeuristicMixed(participantLogEntry);
	}

	private void whenTerminatedWithHeuristicCommit() throws LogException {
		sut.terminatedWithHeuristicCommit(participantLogEntry);
	}

	private void thenRepositoryWasUpdated() throws IllegalArgumentException, LogWriteException {
		Mockito.verify(logRepository, Mockito.times(1)).put(
				Mockito.anyString(), (CoordinatorLogEntry) Mockito.any());

	}

	private void whenTerminated() {
		
		sut.terminated(participantLogEntry);
	}

	private void thenPutWasNotCalledOnRepository() throws IllegalArgumentException, LogException {
		Mockito.verify(logRepository, Mockito.never()).put(Mockito.anyString(),
				(CoordinatorLogEntry) Mockito.any());
	}

	private void givenCoordinatorInRepository(TxState state, boolean expired) throws LogReadException {
		participantLogEntry = newParticipantLogEntryInState(state, expired);
		ParticipantLogEntry[] participantLogEntries = { participantLogEntry };
		CoordinatorLogEntry coordinatorLogEntry = new CoordinatorLogEntry(
				participantLogEntry.coordinatorId, participantLogEntries);

		Mockito.when(logRepository.get(Mockito.anyString())).thenReturn(
				coordinatorLogEntry);
		if(state == TxState.COMMITTING) {
			Mockito.when(logRepository.findAllCommittingParticipants()).thenReturn(Arrays.asList(participantLogEntry));
		}

	}

	private void givenCoordinatorInRepository(TxState state, boolean expired,
			TxState... otherParticipantsStates) throws LogReadException {
		ParticipantLogEntry[] participantLogEntries = new ParticipantLogEntry[otherParticipantsStates.length + 1];
		participantLogEntry = newParticipantLogEntryInState(state, expired);
		participantLogEntries[0] = participantLogEntry;
		for (int i = 0; i < otherParticipantsStates.length; i++) {
			participantLogEntries[i + 1] = newParticipantLogEntryInState(
					otherParticipantsStates[i], expired);
			//System.out.println(participantLogEntries[i + 1].state);
		}

		CoordinatorLogEntry coordinatorLogEntry = new CoordinatorLogEntry(
				participantLogEntry.coordinatorId, participantLogEntries);

		Mockito.when(logRepository.get(Mockito.anyString())).thenReturn(
				coordinatorLogEntry);

	}

	private void thenRepositoryContainsCoordinatorLogEntry(TxState state) throws IllegalArgumentException, LogWriteException {
		ArgumentCaptor<CoordinatorLogEntry> captor = ArgumentCaptor
				.forClass(CoordinatorLogEntry.class);
		Mockito.verify(logRepository, Mockito.times(1))
				.put(Mockito.eq(participantLogEntry.coordinatorId),
						captor.capture());
		Assert.assertEquals(state, captor.getValue().getResultingState());
	}

	private void givenEmptyRepository() {
		participantLogEntry = newParticipantLogEntryInState(TxState.IN_DOUBT, NON_EXPIRED);
	}

	private void whenPresumedAborting() throws IllegalStateException, LogException {
		ParticipantLogEntry entry = createEquivalentParticipantLogEntryForPresumedAbort();
		sut.presumedAborting(entry);
	}

	private ParticipantLogEntry createEquivalentParticipantLogEntryForPresumedAbort() {
		ParticipantLogEntry ret = new ParticipantLogEntry(
				participantLogEntry.coordinatorId, participantLogEntry.participantUri, 
				0, "bla", TxState.IN_DOUBT);
		
		return ret;
	}

	private ParticipantLogEntry newParticipantLogEntryInState(TxState state,
			boolean expired) {
		long expires = Long.MAX_VALUE;
		if (expired) {
			expires = 0;
		}
		return new ParticipantLogEntry("tid", uniqueIdMgr.get(), expires,
				"description", state);

	}
}
