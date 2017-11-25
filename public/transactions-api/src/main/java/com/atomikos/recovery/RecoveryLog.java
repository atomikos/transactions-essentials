/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;

import java.util.Collection;

 /**
  * Handle to the transaction logs for recovery purposes.
  */

public interface RecoveryLog {

	void terminated(ParticipantLogEntry entry);

	void terminatedWithHeuristicRollback(ParticipantLogEntry entry) throws LogException;

	Collection<ParticipantLogEntry> getCommittingParticipants() throws LogReadException;

	void presumedAborting(ParticipantLogEntry entry) throws IllegalStateException, LogException;

	void terminatedWithHeuristicCommit(ParticipantLogEntry entry) throws LogException;

	void terminatedWithHeuristicHazard(ParticipantLogEntry entry) throws LogWriteException;

	void terminatedWithHeuristicMixed(ParticipantLogEntry entry) throws LogException;

	void close(long timeout);
}
