package com.atomikos.recovery.imp;

import static org.junit.Assert.*;

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
		thenCoordinatorLogEntryWasPutInRepository(TxState.IN_DOUBT);
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
		thenCoordinatorLogEntryWasPutInRepository(TxState.ABORTING);
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
		givenActivePendingTransactionWithIndoubtSubTx();
		whenSubTxRecovered();
		thenSubTxWasTerminated();
	}
	
	@Test
	public void testRecoveryOfInDoubtSubTxForInDoubtParent() throws Exception {
		givenInDoubtParentTxWithInDoubtSubTx();
		try {
			whenSubTxRecovered();
		} catch (IllegalStateException ok) {
		}
		thenSubTxIsStillInDoubt();
	}
	
	

	private void thenSubTxIsStillInDoubt() throws IllegalArgumentException, LogWriteException {
		thenRepositoryWasNotUpdated();
	}

	private void thenRepositoryWasNotUpdated() throws IllegalArgumentException, LogWriteException {
		Mockito.verify(logRepository, Mockito.never()).put(
				Mockito.eq(TID), (CoordinatorLogEntry) Mockito.any());
	}

	private void givenInDoubtParentTxWithInDoubtSubTx() throws LogReadException {
		//parent : expired, indoubt parent Tx
		ParticipantLogEntry	parentParticipantLogEntry =  new ParticipantLogEntry("superiorCoordId", TID, 0,
				"description", TxState.IN_DOUBT);
		ParticipantLogEntry[] parentParticipantLogEntries = { parentParticipantLogEntry };
		CoordinatorLogEntry parentCoordinatorLogEntry = new CoordinatorLogEntry(
				"superiorCoordId",false, parentParticipantLogEntries);
		
		Mockito.when(logRepository.get("superiorCoordId")).thenReturn(parentCoordinatorLogEntry);

		
		//SubTx
		givenCoordinatorInRepository("superiorCoordId", TxState.IN_DOUBT, EXPIRED);
		
	
		
	}

	private void thenSubTxWasTerminated() throws IllegalArgumentException, LogWriteException {
		thenRepositoryDoesNotContainCoordinatorLogEntry();
		
	}

	private void thenRepositoryDoesNotContainCoordinatorLogEntry() {
		Assert.assertTrue(sut.getCoordinatorLogEntries().length==0);
	}

	private void whenSubTxRecovered() throws LogException {
		whenPresumedAborting();
		whenTerminated();
	}

	private void givenActivePendingTransactionWithIndoubtSubTx() throws LogReadException {
		// 1 Participant "InDoubt" and superiorCoordId not null
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
			//System.out.println(participantLogEntries[i + 1].state);
		}

		CoordinatorLogEntry coordinatorLogEntry = new CoordinatorLogEntry(
				participantLogEntry.coordinatorId, participantLogEntries);

		Mockito.when(logRepository.get(Mockito.anyString())).thenReturn(
				coordinatorLogEntry);

	}

	private void thenCoordinatorLogEntryWasPutInRepository(TxState state) throws IllegalArgumentException, LogWriteException {
		ArgumentCaptor<CoordinatorLogEntry> captor = ArgumentCaptor
				.forClass(CoordinatorLogEntry.class);
		Mockito.verify(logRepository, Mockito.times(1))
				.put(Mockito.eq(TID),
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
		return new ParticipantLogEntry(TID, uniqueIdMgr.get(), expires,
				"description", state);

	}
}
