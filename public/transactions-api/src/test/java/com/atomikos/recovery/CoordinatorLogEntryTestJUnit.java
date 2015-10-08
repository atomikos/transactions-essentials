package com.atomikos.recovery;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import com.atomikos.icatch.TxState;

public class CoordinatorLogEntryTestJUnit {

	String tid = "TID";
	
	private CoordinatorLogEntry coordinatorLogEntry;

	
	 
	private void givenCoordinatorLogEntryWithParticipantStates(TxState... states) {
		coordinatorLogEntry = createCoordinatorLogEntryWithParticipantsInState(states);
	}
	
	
	
	private void thenCombinedStateIs(TxState expected) {
		TxState combined = coordinatorLogEntry.getResultingState();
		assertEquals(expected, combined);
	}
	
	@Test
	public void testAtLeastOneCommittingParticipantMeansResultCommitting() {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_MIXED, TxState.COMMITTING);
		thenCombinedStateIs(TxState.COMMITTING);
	}
	
	@Test
	public void testAtLeastOneAbortingParticipantMeansResultAborting() {
		givenCoordinatorLogEntryWithParticipantStates(TxState.ABORTING, TxState.HEUR_HAZARD, TxState.HEUR_MIXED);
		thenCombinedStateIs(TxState.ABORTING);
	}
	@Test
	public void testAllParticipantsTerminatedMeansResultTerminated() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.TERMINATED, TxState.TERMINATED);
		thenCombinedStateIs(TxState.TERMINATED);
	}
	
	@Test
	public void testAllParticipantsHeurHazardMeansResultHeurHazard() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_HAZARD, TxState.HEUR_HAZARD);
		thenCombinedStateIs(TxState.HEUR_HAZARD);
	}

	@Test
	public void testAllParticipantsHeurAbortedMeansResultHeurAborted() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_ABORTED, TxState.HEUR_ABORTED);
		thenCombinedStateIs(TxState.HEUR_ABORTED);
	}

	@Test
	public void testAllParticipantsHeurCommittedMeansResultHeurCommitted() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_COMMITTED, TxState.HEUR_COMMITTED);
		thenCombinedStateIs(TxState.HEUR_COMMITTED);
	}
	
	
	@Test
	public void testAllParticipantsIndoubtMeansResultIndoubt() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.IN_DOUBT, TxState.IN_DOUBT);
		thenCombinedStateIs(TxState.IN_DOUBT);
	}
	
	@Test
	public void testDefaultMeansResultHeurMixed() throws Exception {
		
		givenCoordinatorLogEntryWithParticipantStates(TxState.TERMINATED, TxState.HEUR_HAZARD,TxState.HEUR_ABORTED);
		thenCombinedStateIs(TxState.HEUR_MIXED);
	}
	

	@Test
	public void testTransitionFromNullToAbortingIsNotAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.ABORTING);
		thenTransitionNotAllowedFrom(null);
	}
	
	@Test
	public void testTransitionFromInDoubtToAbortingIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.ABORTING);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.IN_DOUBT));
	}
	
	
	@Test
	public void testTransitionFromInDoubtToCommittingIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.COMMITTING);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.IN_DOUBT));
	}
	
	@Test
	public void testTransitionFromCommittingToCommittingIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.COMMITTING);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.COMMITTING));
	}
	
	@Test
	public void testTransitionFromAbortingToAbortingIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.ABORTING);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.ABORTING));
	}
	
	@Test
	public void testTransitionFromCommittingToAbortingIsNotAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.ABORTING);
		thenTransitionNotAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.COMMITTING));
	}
	
	@Test
	public void testTransitionFromNullToCommittingIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.COMMITTING);
		thenTransitionAllowedFrom(null);
	}
	
	@Test
	public void testTransitionFromNullToInDoubtIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.IN_DOUBT);
		thenTransitionAllowedFrom(null);
	}
	

	@Test
	public void testTransitionFromAbortingToCommittingIsNotAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.COMMITTING);
		thenTransitionNotAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.ABORTING));
	}

	
	private void thenTransitionAllowedFrom(CoordinatorLogEntry prior) {
		assertTrue(coordinatorLogEntry.transitionAllowedFrom(prior));
	}

	private void thenTransitionNotAllowedFrom(CoordinatorLogEntry prior) {
		assertFalse(coordinatorLogEntry.transitionAllowedFrom(prior));
	}
	
	

	private CoordinatorLogEntry createCoordinatorLogEntryWithParticipantsInState(TxState... states) {
		ParticipantLogEntry[] participantDetails = new ParticipantLogEntry[states.length];
		for (int i = 0; i < participantDetails.length; i++) {
			participantDetails[i] = new ParticipantLogEntry(tid, "uri", 0,"description", states[i]);
		}
		return new CoordinatorLogEntry(tid,participantDetails);
	}
	
	
}
