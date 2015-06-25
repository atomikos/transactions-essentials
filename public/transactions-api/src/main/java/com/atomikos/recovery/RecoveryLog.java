package com.atomikos.recovery;

import java.util.Collection;


public interface RecoveryLog {

	void terminated(ParticipantLogEntry entry);

	void terminatedWithHeuristicRollback(ParticipantLogEntry entry);

	Collection<ParticipantLogEntry> getCommittingParticipants();

	void presumedAborting(ParticipantLogEntry entry) throws IllegalStateException;

	void terminatedWithHeuristicCommit(ParticipantLogEntry entry);

	void terminatedWithHeuristicHazard(ParticipantLogEntry entry);

	void terminatedWithHeuristicMixed(ParticipantLogEntry entry);

	
}
