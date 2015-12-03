package com.atomikos.recovery;

import static com.atomikos.icatch.TxState.ABORTING;
import static com.atomikos.icatch.TxState.COMMITTING;
import static com.atomikos.icatch.TxState.HEUR_ABORTED;
import static com.atomikos.icatch.TxState.HEUR_COMMITTED;
import static com.atomikos.icatch.TxState.HEUR_HAZARD;
import static com.atomikos.icatch.TxState.HEUR_MIXED;
import static com.atomikos.icatch.TxState.IN_DOUBT;
import static com.atomikos.icatch.TxState.TERMINATED;

import java.io.Serializable;

import com.atomikos.icatch.TxState;

public class CoordinatorLogEntry  implements Serializable {

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
				ret[i] = new ParticipantLogEntry(
						participantLogEntry.coordinatorId,
						participantLogEntry.participantUri,
						participantLogEntry.expires,
						participantLogEntry.description,
						participantLogEntry.state);
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
		if (existing == null) {
			return thisState.isOneOf(COMMITTING, IN_DOUBT, TERMINATED);
		}
		return existing.getResultingState().transitionAllowedTo(thisState);
	}

	public CoordinatorLogEntry presumedAborting(ParticipantLogEntry entry)
			throws IllegalStateException {

		if (!getResultingState().transitionAllowedTo(TxState.ABORTING)) {
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

	public CoordinatorLogEntry terminated(ParticipantLogEntry entry) {
		CoordinatorLogEntry ret = new CoordinatorLogEntry(this,
				new ParticipantLogEntry(entry.coordinatorId,
						entry.participantUri, entry.expires, entry.description,
						TxState.TERMINATED));
		return ret;
	}

	public CoordinatorLogEntry terminatedWithHeuristicCommit(
			ParticipantLogEntry entry) {
		CoordinatorLogEntry ret = new CoordinatorLogEntry(this,
				new ParticipantLogEntry(entry.coordinatorId,
						entry.participantUri, entry.expires, entry.description,
						TxState.HEUR_COMMITTED));
		return ret;
	}

	public CoordinatorLogEntry terminatedWithHeuristicMixed(
			ParticipantLogEntry entry) {
		CoordinatorLogEntry ret = new CoordinatorLogEntry(this,
				new ParticipantLogEntry(entry.coordinatorId,
						entry.participantUri, entry.expires, entry.description,
						TxState.HEUR_MIXED));
		return ret;
	}

	public CoordinatorLogEntry terminatedWithHeuristicRollback(
			ParticipantLogEntry entry) {
		CoordinatorLogEntry ret = new CoordinatorLogEntry(this,
				new ParticipantLogEntry(entry.coordinatorId,
						entry.participantUri, entry.expires, entry.description,
						TxState.HEUR_ABORTED));
		return ret;
	}

	public boolean shouldSync() {
		TxState state = getResultingState();
		switch (state) {
		case IN_DOUBT:
		case ABORTING:
		case TERMINATED:
			return false; // sub-transactions: root will sync COMMITTING entry in same log later which will also sync this entry
		default:
			return !state.isFinalState();
		}
	}

	public CoordinatorLogEntry markAsTerminated() {
		CoordinatorLogEntry coordinatorLogEntry = this;
		for (ParticipantLogEntry participantLogEntry : participantDetails) {
			coordinatorLogEntry = coordinatorLogEntry.terminated(participantLogEntry);
		}
		//not "this" since at least one participant entry must be present
		return coordinatorLogEntry;
		
	}

}
