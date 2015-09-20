package com.atomikos.recovery;

import static org.junit.Assert.*;

import org.junit.Test;

import com.atomikos.icatch.TxState;

public class CoordinatorLogEntryTestJUnit {

	String tid = "TID";
	
	@Test
	public void atLeastOneCommittingParticipantMeansCommitResultCommitting() {
		//given
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryWithParticipantsInStare(TxState.HEUR_MIXED, TxState.COMMITTING);
		//when
		TxState combined = coordinatorLogEntry.getCommitResult();
		
		//Then
		assertEquals(TxState.COMMITTING, combined);
	}
	
	@Test
	public void allParticipantsTerminatedMeansCommitResultTerminated() throws Exception {
		//given
		
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryWithParticipantsInStare(TxState.TERMINATED, TxState.TERMINATED);
		//when
		TxState combined = coordinatorLogEntry.getCommitResult();
		
		//Then
		assertEquals(TxState.TERMINATED, combined);
	}
	
	
	@Test
	public void allParticipantsHeurHazardMeansCommitResultHeurHazard() throws Exception {
		//given
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryWithParticipantsInStare(TxState.HEUR_HAZARD, TxState.HEUR_HAZARD);
		//when
		TxState combined = coordinatorLogEntry.getCommitResult();
		//Then
		assertEquals(TxState.HEUR_HAZARD, combined);
	}

	@Test
	public void allParticipantsHeurAbortedMeansCommitResultHeurAborted() throws Exception {
		//given
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryWithParticipantsInStare(TxState.HEUR_ABORTED, TxState.HEUR_ABORTED);
		//when
		TxState combined = coordinatorLogEntry.getCommitResult();
		//Then
		assertEquals(TxState.HEUR_ABORTED, combined);
	}

	
	@Test
	public void defaultMeansCommitResultHeurMixed() throws Exception {
		
		//given
		CoordinatorLogEntry coordinatorLogEntry = createCoordinatorLogEntryWithParticipantsInStare(TxState.TERMINATED, TxState.HEUR_HAZARD,TxState.HEUR_ABORTED);
		//when
		TxState combined = coordinatorLogEntry.getCommitResult();
		//Then
		assertEquals(TxState.HEUR_MIXED, combined);
	}
	
	
	

	private CoordinatorLogEntry createCoordinatorLogEntryWithParticipantsInStare(TxState... states) {
		ParticipantLogEntry[] participantDetails = new ParticipantLogEntry[states.length];
		for (int i = 0; i < participantDetails.length; i++) {
			participantDetails[i] = new ParticipantLogEntry(tid, "uri", 0,"description", states[i]);
		}
		return new CoordinatorLogEntry(tid,participantDetails);
	}
}
