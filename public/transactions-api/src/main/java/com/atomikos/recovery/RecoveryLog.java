package com.atomikos.recovery;

import java.util.Collection;


public interface RecoveryLog {

	void terminated(ParticipantLogEntry entry);

	void terminatedWithHeuristicRollback(ParticipantLogEntry entry) throws LogWriteException;

	Collection<ParticipantLogEntry> getCommittingParticipants() throws LogReadException;

	void presumedAborting(ParticipantLogEntry entry) throws IllegalStateException, LogWriteException;

	void terminatedWithHeuristicCommit(ParticipantLogEntry entry) throws LogWriteException;

	void terminatedWithHeuristicHazard(ParticipantLogEntry entry) throws LogWriteException;

	void terminatedWithHeuristicMixed(ParticipantLogEntry entry) throws LogWriteException;

	
}
