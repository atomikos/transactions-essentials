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

	private CoordinatorLogEntry(CoordinatorLogEntry toCopy,
			ParticipantLogEntry participantLogEntry) {
		this(toCopy.coordinatorId, toCopy.wasCommitted, copy(
				toCopy.participantDetails, participantLogEntry));
	}

	private static ParticipantLogEntry[] copy(ParticipantLogEntry[] origin,
			ParticipantLogEntry toUpdate) {
		ParticipantLogEntry[] ret = new ParticipantLogEntry[origin.length];
		for (int i = 0; i < origin.length; i++) {
			ParticipantLogEntry participantLogEntry = origin[i];
			if (participantLogEntry.equals(toUpdate)) {
				ret[i] = toUpdate;
			} else {
				ret[i] = new ParticipantLogEntry(participantLogEntry.coordinatorId, participantLogEntry.participantUri, participantLogEntry.expires, participantLogEntry.description, participantLogEntry.state);
			}

		}
		return ret;
	}

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

	
	
	public CoordinatorLogEntry presumedAborting(ParticipantLogEntry entry) throws IllegalStateException {
		
		if(!getResultingState().transitionAllowedTo(TxState.ABORTING)) {
			throw new IllegalStateException();
		}
		
		if (expires() > System.currentTimeMillis()) {
			throw new IllegalStateException();
		}
		
		CoordinatorLogEntry ret = new CoordinatorLogEntry(this,
				new ParticipantLogEntry(entry.coordinatorId,
						entry.participantUri, entry.expires, entry.description,
						TxState.ABORTING));

		return ret;
	}

	public long expires() {
		long expires = Long.MAX_VALUE;
		for (ParticipantLogEntry participantLogEntry : participantDetails) {
			expires = Math.min(expires, participantLogEntry.expires);
		}
		return expires;
	}

}
