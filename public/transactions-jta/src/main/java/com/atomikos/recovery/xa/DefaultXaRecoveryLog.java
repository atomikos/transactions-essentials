package com.atomikos.recovery.xa;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.xa.Xid;

import com.atomikos.datasource.xa.XID;
import com.atomikos.icatch.TxState;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.ParticipantLogEntry;
import com.atomikos.recovery.RecoveryLog;

public class DefaultXaRecoveryLog implements XaRecoveryLog {

	private RecoveryLog log;

	public DefaultXaRecoveryLog(RecoveryLog log) {
		this.log = log;
	}

	@Override
	public void presumedAborting(Xid xid) throws IllegalStateException, LogWriteException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid, TxState.IN_DOUBT);
		log.presumedAborting(entry);
	}

	@Override
	public void terminated(Xid xid) {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid, TxState.TERMINATED);
		log.terminated(entry);
	}

	private ParticipantLogEntry convertXidToParticipantLogEntry(Xid xid, TxState state) {
		//TODO : description should include unique resource name...
		ParticipantLogEntry entry = new ParticipantLogEntry(
				XID.getGlobalTransactionIdAsString(xid),
				XID.getBranchQualifierAsString(xid), 0, xid.toString(),
				state
			);
		return entry;
	}

	@Override
	public Set<Xid> getExpiredCommittingXids() throws LogReadException {
		Set<Xid> ret = new HashSet<Xid>();
		Collection<ParticipantLogEntry> entries = log.getCommittingParticipants();
		for (ParticipantLogEntry entry : entries) {
			if (expired(entry) && !http(entry)) {
				XID xid = new XID(entry.coordinatorId, entry.participantUri);
				ret.add(xid);
			}
		}
		return ret;
	}

	private boolean http(ParticipantLogEntry entry) {
		return entry.participantUri.startsWith("http");
	}

	private boolean expired(ParticipantLogEntry entry) {
		long now = System.currentTimeMillis();
		return now > entry.expires;
	}

	@Override
	public void terminatedWithHeuristicHazardByResource(Xid xid) throws LogWriteException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid, TxState.HEUR_HAZARD);
		log.terminatedWithHeuristicHazard(entry);
	}

	@Override
	public void terminatedWithHeuristicCommitByResource(Xid xid) throws LogWriteException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid, TxState.HEUR_COMMITTED);
		log.terminatedWithHeuristicCommit(entry);
	}

	@Override
	public void terminatedWithHeuristicMixedByResource(Xid xid) throws LogWriteException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid, TxState.HEUR_MIXED);
		log.terminatedWithHeuristicMixed(entry);
	}

	@Override
	public void terminatedWithHeuristicRollbackByResource(Xid xid) throws LogWriteException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid, TxState.HEUR_ABORTED);
		log.terminatedWithHeuristicRollback(entry);
	}

}
