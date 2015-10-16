package com.atomikos.recovery;

import junit.framework.Assert;

import org.junit.Test;

import com.atomikos.icatch.TxState;

public class CoordinatorLogEntryRecoveryTestJUnit {

	String tid = "TID";
	
	private CoordinatorLogEntry coordinatorLogEntryBeforeRecovery;
	private CoordinatorLogEntry coordinatorLogEntryAfterRecovery;
	
	
	@Test
	public void testPresumedAbortingWorksForExpiredInDoubt() throws Exception {
		givenExpiredCoordinatorLogEntryWithParticipantStates(TxState.IN_DOUBT);
		whenPresumedAborting();
		thenPresumedAbortingReturnsAbortingCoordinator();
	}
	

	@Test(expected=IllegalStateException.class)
	public void testPresumedAbortingThrowsForNonExpiredInDoubt() throws Exception {
		givenNonExpiredCoordinatorLogEntryWithParticipantStates(TxState.IN_DOUBT);
		whenPresumedAborting();
	}

	@Test(expected=IllegalStateException.class)
	public void testPresumedAbortingThrowsForNonExpiredCommitting() throws Exception {
		givenNonExpiredCoordinatorLogEntryWithParticipantStates(TxState.COMMITTING);
		whenPresumedAborting();
	}
	
	
	@Test(expected=IllegalStateException.class)
	public void testPresumedAbortingThrowsForExpiredCommitting() throws Exception {
		givenExpiredCoordinatorLogEntryWithParticipantStates(TxState.COMMITTING);
		whenPresumedAborting();
	}
	
	
	private void givenNonExpiredCoordinatorLogEntryWithParticipantStates(TxState states) {
		coordinatorLogEntryBeforeRecovery = createCoordinatorLogEntryWithParticipantsInState(Long.MAX_VALUE, states);
	}


	private void thenPresumedAbortingReturnsAbortingCoordinator() {
		Assert.assertEquals(TxState.ABORTING, coordinatorLogEntryAfterRecovery.getResultingState());
	}


	private void whenPresumedAborting() {
		ParticipantLogEntry participantLogEntry = new ParticipantLogEntry(tid, "uri", 0, "description", TxState.IN_DOUBT);
		coordinatorLogEntryAfterRecovery = coordinatorLogEntryBeforeRecovery.presumedAborting(participantLogEntry);
		
	}

	private void givenExpiredCoordinatorLogEntryWithParticipantStates(TxState... states) {
		coordinatorLogEntryBeforeRecovery = createCoordinatorLogEntryWithParticipantsInState(0,states);
	}

	private CoordinatorLogEntry createCoordinatorLogEntryWithParticipantsInState(long expires,TxState... states) {
		ParticipantLogEntry[] participantDetails = new ParticipantLogEntry[states.length];
		for (int i = 0; i < participantDetails.length; i++) {
			participantDetails[i] = new ParticipantLogEntry(tid, "uri", expires,"description", states[i]);
		}
		return new CoordinatorLogEntry(tid, participantDetails);
	}
}
