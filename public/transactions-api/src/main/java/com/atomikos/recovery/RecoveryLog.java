package com.atomikos.recovery;

import java.util.Collection;


public interface RecoveryLog {

	void terminated(ParticipantLogEntry entry);

	void terminatedWithHeuristicRollback(ParticipantLogEntry entry) throws LogException;

	Collection<ParticipantLogEntry> getCommittingParticipants() throws LogReadException;

	void presumedAborting(ParticipantLogEntry entry) throws IllegalStateException, LogException;

	void terminatedWithHeuristicCommit(ParticipantLogEntry entry) throws LogException;

	void terminatedWithHeuristicHazard(ParticipantLogEntry entry) throws LogWriteException;

	void terminatedWithHeuristicMixed(ParticipantLogEntry entry) throws LogException;

	
}
