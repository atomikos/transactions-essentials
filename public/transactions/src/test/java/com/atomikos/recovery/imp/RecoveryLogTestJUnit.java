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

	private static final String TID = "tid";
	private static final boolean EXPIRED = true;
	private static final boolean NON_EXPIRED = false;
	private RecoveryLogImp sut;
	private CoordinatorLogEntryRepository logRepository;
	ParticipantLogEntry participantLogEntry;
	
	Collection<ParticipantLogEntry> committingParticipants;

	private UniqueIdMgr uniqueIdMgr = new UniqueIdMgr(getClass().getName());

	@Before
	public void configure() {
		sut = new RecoveryLogImp();
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
		thenCoordinatorLogEntryWasPutInRepository(TID, 1,TxState.IN_DOUBT);
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
		thenCoordinatorLogEntryWasPutInRepository(TID, 1, TxState.ABORTING);
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
	
	@Test
	public void testRecoveryOfPendingInDoubtSubTx() throws Exception {
		boolean recoveryShouldCommit = false;
		givenActivePendingTransactionWithIndoubtSubTx();
		whenSubTxRecovered(recoveryShouldCommit);
		thenSubTxWasTerminated(recoveryShouldCommit);
	}
	
	@Test
	public void testRecoveryOfInDoubtSubTxForNonExpiredInDoubtParent() throws Exception {
		givenNonExpiredInDoubtParentTxWithInDoubtSubTx();
		try {
			whenSubTxRecovered(false);
		} catch (IllegalStateException ok) {
		}
		thenSubTxIsStillInDoubt();
	}
	
	@Test
	public void testRecoveryOfInDoubtSubTxForCommittingParent() throws Exception {
		boolean recoveryShouldCommit = true;
		givenCommittingParentTxWithInDoubtSubTx();
		whenSubTxRecovered(recoveryShouldCommit);
		thenParentTxWasTerminated(recoveryShouldCommit);
		thenSubTxWasTerminated(recoveryShouldCommit);
	}
	
	@Test
	public void testRecoveryOfInDoubtSubTxForExpiredInDoubtParent() throws Exception {
		givenExpiredInDoubtParentTxWithInDoubtSubTx();
		boolean recoveryShouldCommit = false;
		try {
			
			whenSubTxRecovered(recoveryShouldCommit);
		} catch (IllegalStateException ok) {
		}
		thenParentTxWasTerminated(recoveryShouldCommit);
		thenSubTxWasTerminated(recoveryShouldCommit);
	}

	

	private void thenParentTxWasTerminated(boolean recoveryShouldCommit) throws IllegalArgumentException, LogWriteException {
		int expectedNumberOfInvocations = recoveryShouldCommit ? 1 : 2;
		thenCoordinatorLogEntryWasPutInRepository("superiorCoordId", expectedNumberOfInvocations,TxState.TERMINATED);
	}

	private void givenCommittingParentTxWithInDoubtSubTx() throws LogReadException {
		givenParentTransactionInState(TxState.COMMITTING, NON_EXPIRED);
		givenCoordinatorInRepository("superiorCoordId", TxState.IN_DOUBT, EXPIRED);
	}
	CoordinatorLogEntry parentCoordinatorLogEntry;
	protected void givenParentTransactionInState(TxState state, boolean expired) throws LogReadException {
		long expires = Long.MAX_VALUE;
		if (expired) {
			expires = 0;
		}
		
		ParticipantLogEntry	parentParticipantLogEntry =  new ParticipantLogEntry("superiorCoordId", TID, expires,
						"description", state);
		ParticipantLogEntry[] parentParticipantLogEntries = { parentParticipantLogEntry };
		parentCoordinatorLogEntry = new CoordinatorLogEntry(
						"superiorCoordId",false, parentParticipantLogEntries);
				
		Mockito.when(logRepository.get("superiorCoordId")).thenReturn(parentCoordinatorLogEntry);
	}

	private void thenSubTxIsStillInDoubt() throws IllegalArgumentException, LogWriteException {
		thenRepositoryWasNotUpdated();
	}

	private void thenRepositoryWasNotUpdated() throws IllegalArgumentException, LogWriteException {
		Mockito.verify(logRepository, Mockito.never()).put(
				Mockito.eq(TID), (CoordinatorLogEntry) Mockito.any());
	}

	private void givenNonExpiredInDoubtParentTxWithInDoubtSubTx() throws LogReadException {
		givenParentTransactionInState(TxState.IN_DOUBT, NON_EXPIRED);
		givenCoordinatorInRepository("superiorCoordId", TxState.IN_DOUBT, EXPIRED);
	}

	private void givenExpiredInDoubtParentTxWithInDoubtSubTx() throws LogReadException {
		givenParentTransactionInState(TxState.IN_DOUBT, EXPIRED);
		givenCoordinatorInRepository("superiorCoordId", TxState.IN_DOUBT, EXPIRED);
	}
	
	private void thenSubTxWasTerminated(boolean commitExpected) throws IllegalArgumentException, LogWriteException {
		int expectedNumberOfInvocations = commitExpected ? 1 : 2;
		thenCoordinatorLogEntryWasPutInRepository(TID,expectedNumberOfInvocations,TxState.TERMINATED);	
	}

	

	private void whenSubTxRecovered(boolean recoveryShouldCommit) throws LogException {
		if (!recoveryShouldCommit){
			whenPresumedAborting();
		}
		whenTerminated();
	}

	private void givenActivePendingTransactionWithIndoubtSubTx() throws LogReadException {
		givenCoordinatorInRepository("superiorCoordId",TxState.IN_DOUBT, EXPIRED);
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
		givenCoordinatorInRepository(null, state, expired);	
	}

	private void givenCoordinatorInRepository(String superioCoordinatorId, TxState state, boolean expired) throws LogReadException {
		participantLogEntry = newParticipantLogEntryInState(state, expired);
		ParticipantLogEntry[] participantLogEntries = { participantLogEntry };
		CoordinatorLogEntry coordinatorLogEntry = new CoordinatorLogEntry(
				TID,false, participantLogEntries, superioCoordinatorId);

		Mockito.when(logRepository.get(TID)).thenReturn(
				coordinatorLogEntry);
		if(state == TxState.COMMITTING) {
			Mockito.when(logRepository.findAllCommittingCoordinatorLogEntries()).thenReturn(Arrays.asList(coordinatorLogEntry));
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
		}

		CoordinatorLogEntry coordinatorLogEntry = new CoordinatorLogEntry(
				participantLogEntry.coordinatorId, participantLogEntries);

		Mockito.when(logRepository.get(Mockito.anyString())).thenReturn(
				coordinatorLogEntry);

	}

	private void thenCoordinatorLogEntryWasPutInRepository(String coordinatorId, int expectedNumberOfInvocations, TxState state) throws IllegalArgumentException, LogWriteException {
		ArgumentCaptor<CoordinatorLogEntry> captor = ArgumentCaptor
				.forClass(CoordinatorLogEntry.class);
		Mockito.verify(logRepository, Mockito.times(expectedNumberOfInvocations))
				.put(Mockito.eq(coordinatorId),
						captor.capture());
		Assert.assertTrue(captor.getValue().getResultingState().isOneOf(state));
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
				participantLogEntry.coordinatorId, participantLogEntry.uri, 0, "bla", TxState.IN_DOUBT);
		
		return ret;
	}

	private ParticipantLogEntry newParticipantLogEntryInState(TxState state, boolean expired) {
		long expires = Long.MAX_VALUE;
		if (expired) {
			expires = 0;
		}
		return new ParticipantLogEntry(TID, uniqueIdMgr.get(), expires,
				"description", state);

	}
}
