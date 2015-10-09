package com.atomikos.recovery;

import static com.atomikos.icatch.TxState.ABORTING;
import static com.atomikos.icatch.TxState.COMMITTING;
import static com.atomikos.icatch.TxState.HEUR_ABORTED;
import static com.atomikos.icatch.TxState.HEUR_COMMITTED;
import static com.atomikos.icatch.TxState.HEUR_HAZARD;
import static com.atomikos.icatch.TxState.HEUR_MIXED;
import static com.atomikos.icatch.TxState.IN_DOUBT;
import static com.atomikos.icatch.TxState.TERMINATED;

import com.atomikos.icatch.TxState;

public class CoordinatorLogEntry {

	public final String coordinatorId;

	public final boolean wasCommitted;

	public final ParticipantLogEntry[] participantDetails;

	public CoordinatorLogEntry(String coordinatorId,
			ParticipantLogEntry[] participantDetails) {
		this(coordinatorId, false, participantDetails);
	}

	public CoordinatorLogEntry(String coordinatorId, boolean wasCommitted,
			ParticipantLogEntry[] participantDetails) {
		this.coordinatorId = coordinatorId;
		this.wasCommitted = wasCommitted;
		this.participantDetails = participantDetails;
	}

	public TxState getResultingState() {
		if (oneParticipantInState(COMMITTING)) {
			return COMMITTING;
		} else if (oneParticipantInState(ABORTING)) {
			return ABORTING;
		} else if (allParticipantsInState(TERMINATED)) {
			return TERMINATED;
		} else if (allParticipantsInState(HEUR_HAZARD)) {
			return HEUR_HAZARD;
		} else if (allParticipantsInState(HEUR_ABORTED)) {
			return HEUR_ABORTED;
		} else if (allParticipantsInState(HEUR_COMMITTED)) {
			return HEUR_COMMITTED;
		} else if (allParticipantsInState(IN_DOUBT)) {
			return IN_DOUBT;
		}
		// the default
		return HEUR_MIXED;
	}

	private boolean allParticipantsInState(TxState state) {
		for (ParticipantLogEntry participantDetail : participantDetails) {
			if (!participantDetail.state.equals(state)) {
				return false;
			}
		}
		return true;
	}

	private boolean oneParticipantInState(TxState state) {
		for (ParticipantLogEntry participantDetail : participantDetails) {
			if (participantDetail.state.equals(state)) {
				return true;
			}
		}
		return false;
	}

	public boolean transitionAllowedFrom(CoordinatorLogEntry existing) {
		TxState thisState = getResultingState();
		switch (thisState) {
		case ABORTING:
			if (existing == null) {
				return false;
			} 
			return existing.getResultingState().transitionAllowedTo(thisState);
		case COMMITTING:
			if (existing == null) {
				return true;
			} 
			return existing.getResultingState().transitionAllowedTo(thisState);
		case IN_DOUBT:
			if (existing == null) {
				return true;
			} 
			return existing.getResultingState().transitionAllowedTo(thisState);
		case TERMINATED:
			if (existing == null) {
				return false;
			}
			return existing.getResultingState().transitionAllowedTo(thisState);
		case HEUR_ABORTED:
		case HEUR_COMMITTED:
		case HEUR_HAZARD:
		case HEUR_MIXED:
			if (existing == null) {
				return false;
			}	
			return existing.getResultingState().transitionAllowedTo(thisState);
		default:
			return false;
		}
		// the default
	}


}
