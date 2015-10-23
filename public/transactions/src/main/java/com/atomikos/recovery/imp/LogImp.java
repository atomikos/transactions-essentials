package com.atomikos.recovery.imp;

import java.util.Collection;

import com.atomikos.icatch.TxState;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.CoordinatorLogEntryRepository;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.OltpLog;
import com.atomikos.recovery.ParticipantLogEntry;
import com.atomikos.recovery.RecoveryLog;

public class LogImp implements OltpLog, RecoveryLog {

	private static final Logger LOGGER = LoggerFactory.createLogger(LogImp.class);
	
	private CoordinatorLogEntryRepository repository;

	public void setRepository(CoordinatorLogEntryRepository repository) {
		this.repository = repository;
	}

	@Override
	public void write(CoordinatorLogEntry coordinatorLogEntry)
			throws IllegalStateException, LogWriteException {
		if (!entryAllowed(coordinatorLogEntry)) {
			throw new IllegalStateException();
		}
		
		repository.put(coordinatorLogEntry.coordinatorId, coordinatorLogEntry);
		

	}

	private boolean entryAllowed(CoordinatorLogEntry coordinatorLogEntry) {
		CoordinatorLogEntry existing = repository
				.get(coordinatorLogEntry.coordinatorId);
		return coordinatorLogEntry.transitionAllowedFrom(existing);
	}

	@Override
	public void remove(String coordinatorId) throws LogWriteException {
		repository.remove(coordinatorId);
	}

	@Override
	public void terminated(ParticipantLogEntry entry)  {
		CoordinatorLogEntry coordinatorLogEntry = repository
				.get(entry.coordinatorId);
		if (coordinatorLogEntry == null) {
			LOGGER.logWarning("termination called on non existent Coordinator "
					+ entry.coordinatorId + " " + entry.participantUri);
		} else {
			CoordinatorLogEntry updated = coordinatorLogEntry.terminated(entry);
			try {
				updateRepository(updated,TxState.TERMINATED);
			} catch (LogWriteException e) {
				LOGGER.logWarning("Unable to write to repository "+entry+" ignoring");
			}
		}

	}

	private void updateRepository(CoordinatorLogEntry updated, TxState... stateForRemoval)
			throws LogWriteException {
		if (updated.getResultingState().isOneOf(stateForRemoval)) {
			repository.remove(updated.coordinatorId);
		} else {
			repository.put(updated.coordinatorId, updated);
		}
	}

	@Override
	public void terminatedWithHeuristicRollback(ParticipantLogEntry entry) throws LogWriteException {

		CoordinatorLogEntry coordinatorLogEntry = repository
				.get(entry.coordinatorId);
		if (coordinatorLogEntry == null) {
			LOGGER.logWarning("terminatedWithHeuristicRollback called on non existent Coordinator "
					+ entry.coordinatorId + " " + entry.participantUri);
		} else {
			CoordinatorLogEntry updated = coordinatorLogEntry.terminatedWithHeuristicRollback(entry);
			updateRepository(updated,TxState.HEUR_MIXED,TxState.HEUR_ABORTED);
		}
	}

	@Override
	public Collection<ParticipantLogEntry> getCommittingParticipants()
			throws LogReadException {

		return repository.findAllCommittingParticipants();
	}

	@Override
	public void presumedAborting(ParticipantLogEntry entry)
			throws IllegalStateException, LogWriteException {
		CoordinatorLogEntry coordinatorLogEntry = repository
				.get(entry.coordinatorId);
		if (coordinatorLogEntry == null) {
			ParticipantLogEntry[] participantDetails = new ParticipantLogEntry[1];
			participantDetails[0] = entry;
			coordinatorLogEntry = new CoordinatorLogEntry(entry.coordinatorId,
					participantDetails);
			repository.put(entry.coordinatorId, coordinatorLogEntry);
			throw new IllegalStateException();
		} else {
			CoordinatorLogEntry updated = coordinatorLogEntry
					.presumedAborting(entry);
			repository.put(updated.coordinatorId, updated);
		}

	}

	@Override
	public void terminatedWithHeuristicCommit(ParticipantLogEntry entry) throws LogWriteException {
		CoordinatorLogEntry coordinatorLogEntry = repository
				.get(entry.coordinatorId);
		if (coordinatorLogEntry == null) {
			LOGGER.logWarning("terminatedWithHeuristicCommit called on non existent Coordinator "
					+ entry.coordinatorId + " " + entry.participantUri);
		} else {
			CoordinatorLogEntry updated = coordinatorLogEntry
					.terminatedWithHeuristicCommit(entry);
			
			updateRepository(updated,TxState.HEUR_MIXED,TxState.HEUR_COMMITTED);
			
		}

	}

	@Override
	public void terminatedWithHeuristicHazard(ParticipantLogEntry entry) {
		LOGGER.logWarning("terminatedWithHeuristicHazard " + entry);
	}

	@Override
	public void terminatedWithHeuristicMixed(ParticipantLogEntry entry) throws LogWriteException {
		CoordinatorLogEntry coordinatorLogEntry = repository
				.get(entry.coordinatorId);
		if (coordinatorLogEntry == null) {
			LOGGER.logWarning("terminatedWithHeuristicMixed called on non existent Coordinator "
					+ entry.coordinatorId + " " + entry.participantUri);
		} else {

			CoordinatorLogEntry updated = coordinatorLogEntry
					.terminatedWithHeuristicMixed(entry);
			updateRepository(updated,TxState.HEUR_MIXED);
		}
	}

}
