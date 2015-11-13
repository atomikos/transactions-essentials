package com.atomikos.recovery;

import static org.junit.Assert.*;
import junit.framework.Assert;

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
	public void testTransitionFromInDoubtToInDoubtIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.IN_DOUBT);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.IN_DOUBT));
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
	
	@Test
	public void testTransitionFromNullToTerminatedIsNotAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.TERMINATED);
		thenTransitionNotAllowedFrom(null);
	}
	
	@Test
	public void testTransitionFromCommittingToTerminatedIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.TERMINATED);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.COMMITTING));
	}

	@Test
	public void testTransitionFromAbortingToTerminatedIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.TERMINATED);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.ABORTING));
	}
	
	@Test
	public void testTransitionFromNullToHeurAbortedIsNotAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_ABORTED);
		thenTransitionNotAllowedFrom(null);
	}
	
	@Test
	public void testTransitionFromAbortingToHeurAbortedIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_ABORTED);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.ABORTING));
	}
	
	@Test
	public void testTransitionFromNullToHeurCommittedIsNotAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_COMMITTED);
		thenTransitionNotAllowedFrom(null);
	}
	@Test
	public void testTransitionFromAbortingToHeurCommittedIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_COMMITTED);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.ABORTING));
	}
	@Test
	public void testTransitionFromNullToHeurHazardIsNotAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_HAZARD);
		thenTransitionNotAllowedFrom(null);
	}
	@Test
	public void testTransitionFromAbortingToHeurHazardIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_HAZARD);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.ABORTING));
	}
	
	@Test
	public void testTransitionFromNullToHeurMixedIsNotAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_MIXED);
		thenTransitionNotAllowedFrom(null);
	}
	@Test
	public void testTransitionFromCommittingToHeurMixedIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_MIXED);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.COMMITTING));
	}

	@Test
	public void testTransitionFromCommittingToHeurAbortedIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_ABORTED);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.COMMITTING));
	}
	
	
	@Test
	public void testTransitionFromCommittingToHeurCommittedIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_COMMITTED);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.COMMITTING));
	}
	
	@Test
	public void testTransitionFromComittingToHeurHazardIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_HAZARD);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.COMMITTING));
	}
	
	
	@Test
	public void testTransitionFromAbortingToHeurMixedIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_MIXED);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.ABORTING));
	}

	@Test
	public void testTransitionFromHeurMixedToHeurMixedIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_MIXED);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.HEUR_MIXED));
	}
	
	@Test
	public void testTransitionFromHeurAbortedToHeurAbortedIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_ABORTED);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.HEUR_ABORTED));
	}
	@Test
	public void testTransitionFromHeurCommittedToHeurCommittedIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_COMMITTED);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.HEUR_COMMITTED));
	}
	
	@Test
	public void testTransitionFromHeurHazardToHeurHeurHazardIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.HEUR_HAZARD);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.HEUR_HAZARD));
	}
	@Test
	public void testTransitionFromTerminatedToTerminatedIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.TERMINATED);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.TERMINATED));
	}
	
	@Test
	public void testTransitionFromHeurMixedToTerminatedIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.TERMINATED);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.HEUR_MIXED));
	}
	
	@Test
	public void testTransitionFromHeurAbortedToTerminatedIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.TERMINATED);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.HEUR_ABORTED));
	}
	@Test
	public void testTransitionFromHeurHazardToTerminatedIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.TERMINATED);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.HEUR_HAZARD));
	}
	@Test
	public void testTransitionFromHeurCommittedToTerminatedIsAllowed() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.TERMINATED);
		thenTransitionAllowedFrom(createCoordinatorLogEntryWithParticipantsInState(TxState.HEUR_COMMITTED));
	}
	
	@Test
	public void thenCoordinatorLogEntryExpiresIsTheMinimumOfAllParticipantExpires() throws Exception {
		long expiresMin = System.currentTimeMillis()+10000L;
		long expiresMax = System.currentTimeMillis()+10000000L;
		givenCoordinatorLogEntryWithParticipantExpires(expiresMin,expiresMax);
		
		thenCoordinatorLogEntryExpiresAt(expiresMin);
	}
	
	@Test
	public void testCommittingShouldSync() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.COMMITTING);
		thenCoordinatorLogEntryShouldSync();
	}

	@Test
	public void testInDoubtShouldNotSync() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.IN_DOUBT);
		thenCoordinatorLogEntryShouldNotSync();
	}

	@Test
	public void testAbortingShouldNotSync() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.ABORTING);
		thenCoordinatorLogEntryShouldNotSync();
	}
	
	@Test
	public void testTerminatedShouldNotSync() throws Exception {
		givenCoordinatorLogEntryWithParticipantStates(TxState.TERMINATED);
		thenCoordinatorLogEntryShouldNotSync();
	}
	
	private void thenCoordinatorLogEntryShouldNotSync() {
		Assert.assertFalse(coordinatorLogEntry.shouldSync());
		
	}



	private void thenCoordinatorLogEntryShouldSync() {
		Assert.assertTrue(coordinatorLogEntry.shouldSync());
		
	}



	private void thenCoordinatorLogEntryExpiresAt(long expiresMin) {
		Assert.assertEquals(expiresMin, coordinatorLogEntry.expires());
		
	}



	private void givenCoordinatorLogEntryWithParticipantExpires(long... expires) {
		ParticipantLogEntry[] participantDetails = new ParticipantLogEntry[expires.length];
		for (int i = 0; i < participantDetails.length; i++) {
			participantDetails[i] = new ParticipantLogEntry(tid, "uri", expires[i],"description", TxState.IN_DOUBT);
		}
		
		coordinatorLogEntry = new CoordinatorLogEntry(tid, participantDetails);
		
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
