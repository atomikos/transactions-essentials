package com.atomikos.recovery.imp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.atomikos.icatch.TxState;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.CoordinatorLogEntryRepository;
import com.atomikos.recovery.ParticipantLogEntry;
import com.atomikos.util.UniqueIdMgr;

public class RecoveryLogTestJUnit {

	private static final boolean EXPIRED = true;
	private static final boolean NON_EXPIRED = false;
	private LogImp sut;
	private CoordinatorLogEntryRepository logRepository;
	ParticipantLogEntry participantLogEntry;

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
	public void testTerminatedOfExistingCoordinatorRemovesFromLog()
			throws Exception {
		givenCoordinatorInRepository(TxState.COMMITTING, NON_EXPIRED, TxState.TERMINATED);
		whenTerminated();
		thenRemoveWasCalledOnRepository();
	}

	@Test
	public void testTerminatedForNonExistingCoordinatorDoesNothing() throws Exception {
		givenEmptyRepository();
		whenTerminated();
		thenRepositoryWasNotUpdated();
	}
	
	@Test
	public void testHeuristicCommitForNonExistingCoordinatorDoesNothing() throws Exception {
		givenEmptyRepository();
		whenTerminatedWithHeuristicCommit();
		thenRepositoryWasNotUpdated();
	}
	
	@Test
	public void testHeuristicCommitOfParticipantForExistingCoordinatorUpdatesLog() throws Exception {
		givenCoordinatorInRepository(TxState.HEUR_COMMITTED, NON_EXPIRED, TxState.COMMITTING);
		whenTerminatedWithHeuristicCommit();
		thenRepositoryWasUpdated();
	}
	
	@Test
	public void testHeuristicCommitOfExistingCoordinatorResultingInHeurCommittedRemovesFromLog() throws Exception {
		givenCoordinatorInRepository(TxState.HEUR_COMMITTED, NON_EXPIRED, TxState.HEUR_COMMITTED);
		whenTerminatedWithHeuristicCommit();
		thenRemoveWasCalledOnRepository();
	}
	
	@Test
	public void testHeuristicCommitOfExistingCoordinatorResultingInHeurMixedRemovesFromLog() throws Exception {
		givenCoordinatorInRepository(TxState.HEUR_COMMITTED, NON_EXPIRED, TxState.HEUR_MIXED);
		whenTerminatedWithHeuristicCommit();
		thenRemoveWasCalledOnRepository();
	}
	
	@Test
	public void testHeuristicMixedForNonExistingCoordinatorDoesNothing() throws Exception {
		givenEmptyRepository();
		whenTerminatedWithHeuristicMixed();
		thenRepositoryWasNotUpdated();
	}
	
	@Test
	public void testHeuristicMixedOfExistingCoordinatorResultingInHeurMixedRemovesFromLog() throws Exception {
		givenCoordinatorInRepository(TxState.HEUR_MIXED, NON_EXPIRED);
		whenTerminatedWithHeuristicMixed();
		thenRemoveWasCalledOnRepository();
	}
	
	@Test
	public void testHeuristicMixedOfExistingCoordinatorNotResultingInHeurMixedUpdatesLog() throws Exception {
		givenCoordinatorInRepository(TxState.HEUR_MIXED, NON_EXPIRED, TxState.COMMITTING);
		whenTerminatedWithHeuristicMixed();
		thenRepositoryWasUpdated();
	}

	@Test
	public void testHeuristicRollbackForNonExistingCoordinatorDoesNothing() throws Exception {
		givenEmptyRepository();
		whenTerminatedWithHeuristicRollback();
		thenRepositoryWasNotUpdated();
	}
	
	
	@Test
	public void testHeuristicRollbackOfExistingCoordinatorResultingAbortedRemovesFromLog() throws Exception {
		givenCoordinatorInRepository(TxState.ABORTED, NON_EXPIRED);
		whenTerminatedWithHeuristicRollback();
		thenRemoveWasCalledOnRepository();
	}
	
	@Test
	public void testHeuristicRollbackOfExistingCoordinatorHeurAbortedRemovesFromLog() throws Exception {
		givenCoordinatorInRepository(TxState.COMMITTING, NON_EXPIRED,TxState.HEUR_ABORTED);
		whenTerminatedWithHeuristicRollback();
		thenRemoveWasCalledOnRepository();
	}
	
	@Test
	public void testHeuristicRollbackOfExistingCoordinatorCommittingRemovesFromLog() throws Exception {
		givenCoordinatorInRepository(TxState.COMMITTING, NON_EXPIRED,TxState.COMMITTING);
		whenTerminatedWithHeuristicRollback();
		thenRepositoryWasUpdated();
	}
	
	private void whenTerminatedWithHeuristicRollback() {
		sut.terminatedWithHeuristicRollback(participantLogEntry);
	}

	private void whenTerminatedWithHeuristicMixed() {
		sut.terminatedWithHeuristicMixed(participantLogEntry);
	}

	private void whenTerminatedWithHeuristicCommit() {
		sut.terminatedWithHeuristicCommit(participantLogEntry);
	}

	private void thenRepositoryWasNotUpdated() {
		Mockito.verify(logRepository, Mockito.never()).remove(
				Mockito.anyString());
		Mockito.verify(logRepository, Mockito.never()).put(
				Mockito.anyString(), (CoordinatorLogEntry) Mockito.any());
		
	}

	private void thenRemoveWasCalledOnRepository() {
		Mockito.verify(logRepository, Mockito.times(1)).remove(
				Mockito.anyString());
	}

	private void thenRepositoryWasUpdated() {
		Mockito.verify(logRepository, Mockito.times(1)).put(
				Mockito.anyString(), (CoordinatorLogEntry) Mockito.any());

	}

	private void whenTerminated() {
		
		sut.terminated(participantLogEntry);
	}

	private void thenPutWasNotCalledOnRepository() {
		Mockito.verify(logRepository, Mockito.never()).put(Mockito.anyString(),
				(CoordinatorLogEntry) Mockito.any());
	}

	private void givenCoordinatorInRepository(TxState state, boolean expired) {
		participantLogEntry = newParticipantLogEntryInState(state, expired);
		ParticipantLogEntry[] participantLogEntries = { participantLogEntry };
		CoordinatorLogEntry coordinatorLogEntry = new CoordinatorLogEntry(
				participantLogEntry.coordinatorId, participantLogEntries);

		Mockito.when(logRepository.get(Mockito.anyString())).thenReturn(
				coordinatorLogEntry);

	}

	private void givenCoordinatorInRepository(TxState state, boolean expired,
			TxState... otherParticipantsStates) {
		ParticipantLogEntry[] participantLogEntries = new ParticipantLogEntry[otherParticipantsStates.length + 1];
		participantLogEntry = newParticipantLogEntryInState(state, expired);
		participantLogEntries[0] = participantLogEntry;
		for (int i = 0; i < otherParticipantsStates.length; i++) {
			participantLogEntries[i + 1] = newParticipantLogEntryInState(
					otherParticipantsStates[i], expired);
			System.out.println(participantLogEntries[i + 1].state);
		}

		CoordinatorLogEntry coordinatorLogEntry = new CoordinatorLogEntry(
				participantLogEntry.coordinatorId, participantLogEntries);

		Mockito.when(logRepository.get(Mockito.anyString())).thenReturn(
				coordinatorLogEntry);

	}

	private void thenRepositoryContainsCoordinatorLogEntry(TxState state) {
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

	private void whenPresumedAborting() {
		sut.presumedAborting(participantLogEntry);
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
