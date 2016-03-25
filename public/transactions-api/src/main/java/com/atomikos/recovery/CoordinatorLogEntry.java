/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;

import static com.atomikos.recovery.TxState.ABORTING;
import static com.atomikos.recovery.TxState.COMMITTING;
import static com.atomikos.recovery.TxState.HEUR_ABORTED;
import static com.atomikos.recovery.TxState.HEUR_COMMITTED;
import static com.atomikos.recovery.TxState.HEUR_HAZARD;
import static com.atomikos.recovery.TxState.HEUR_MIXED;
import static com.atomikos.recovery.TxState.IN_DOUBT;
import static com.atomikos.recovery.TxState.TERMINATED;

/**
 * Coordinator snapshot for logging and recovery purposes.
 *
 */
public class CoordinatorLogEntry {

	public final String id;

	public final boolean wasCommitted;

	/**
	 * Only for subtransactions, null otherwise.
	 */
	public final String superiorCoordinatorId;
	
	public final ParticipantLogEntry[] participants;

	private CoordinatorLogEntry(CoordinatorLogEntry toCopy,
			ParticipantLogEntry participantLogEntry) {
		this(toCopy.id, toCopy.wasCommitted, copy(
				toCopy.participants, participantLogEntry));
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
						participantLogEntry.uri,
						participantLogEntry.expires,
						participantLogEntry.resourceName,
						participantLogEntry.state);
			}

		}
		return ret;
	}

	public CoordinatorLogEntry(String coordinatorId,
			ParticipantLogEntry[] participantDetails) {
		this(coordinatorId, false, participantDetails, null);
	}

	public CoordinatorLogEntry(String coordinatorId, boolean wasCommitted,
			ParticipantLogEntry[] participants) {
		this.id = coordinatorId;
		this.wasCommitted = wasCommitted;
		this.participants = participants;
		this.superiorCoordinatorId = null;
	}
	
	public CoordinatorLogEntry(String coordinatorId, boolean wasCommitted,
			ParticipantLogEntry[] participants, String superiorCoordinatorId) {
		this.id = coordinatorId;
		this.wasCommitted = wasCommitted;
		this.participants = participants;
		this.superiorCoordinatorId = superiorCoordinatorId;
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
		for (ParticipantLogEntry participantLogEntry : participants) {
			if (!(participantLogEntry.state == state)) {
				return false;
			}
		}
		return true;
	}

	private boolean oneParticipantInState(TxState state) {
		for (ParticipantLogEntry ParticipantLogEntry : participants) {
			if (ParticipantLogEntry.state == state) {
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
						entry.uri, entry.expires, entry.resourceName,
						TxState.ABORTING));

		return ret;
	}

	public long expires() {
		long expires = Long.MAX_VALUE;
		for (ParticipantLogEntry participant : participants) {
			expires = Math.min(expires, participant.expires);
		}
		return expires;
	}

	public CoordinatorLogEntry terminated(ParticipantLogEntry entry) {
		CoordinatorLogEntry ret = new CoordinatorLogEntry(this,
				new ParticipantLogEntry(entry.coordinatorId,
						entry.uri, entry.expires, entry.resourceName,
						TxState.TERMINATED));
		return ret;
	}

	public CoordinatorLogEntry terminatedWithHeuristicCommit(
			ParticipantLogEntry entry) {
		CoordinatorLogEntry ret = new CoordinatorLogEntry(this,
				new ParticipantLogEntry(entry.coordinatorId,
						entry.uri, entry.expires, entry.resourceName,
						TxState.HEUR_COMMITTED));
		return ret;
	}

	public CoordinatorLogEntry terminatedWithHeuristicMixed(
			ParticipantLogEntry entry) {
		CoordinatorLogEntry ret = new CoordinatorLogEntry(this,
				new ParticipantLogEntry(entry.coordinatorId,
						entry.uri, entry.expires, entry.resourceName,
						TxState.HEUR_MIXED));
		return ret;
	}

	public CoordinatorLogEntry terminatedWithHeuristicRollback(
			ParticipantLogEntry entry) {
		CoordinatorLogEntry ret = new CoordinatorLogEntry(this,
				new ParticipantLogEntry(entry.coordinatorId,
						entry.uri, entry.expires, entry.resourceName,
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
			case COMMITTING:
				return participants.length > 1; //cf. case 22128
			default:
				return !state.isFinalState();
		}
	}

	public CoordinatorLogEntry markAsTerminated() {
		CoordinatorLogEntry coordinatorLogEntry = this;
		for (ParticipantLogEntry participantLogEntry : participants) {
			coordinatorLogEntry = coordinatorLogEntry.terminated(participantLogEntry);
		}
		//not "this" since at least one participant entry must be present
		return coordinatorLogEntry;
		
	}

	public boolean hasExpired() {
		return expires() < System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return "CoordinatorLogEntry [id=" + id + ", wasCommitted=" + wasCommitted + ", state=" + getResultingState() + "]";
	}
	
	


}
