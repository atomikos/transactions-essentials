package com.atomikos.recovery;

import static com.atomikos.icatch.TxState.*;
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

	public TxState getCommitResult() {
		if (oneParticipantInState(COMMITTING)) {
			return COMMITTING;
		} else if (allParticipantsInState(TERMINATED)) {
			return TERMINATED;
		} else if (allParticipantsInState(HEUR_HAZARD)) {
			return HEUR_HAZARD;
		} else if (allParticipantsInState(HEUR_ABORTED)) {
			return HEUR_ABORTED;
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

}
