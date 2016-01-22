package com.atomikos.recovery.xa;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.xa.Xid;

import com.atomikos.datasource.xa.XID;
import com.atomikos.icatch.ParticipantLogEntry;
import com.atomikos.icatch.TxState;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.RecoveryLog;

public class DefaultXaRecoveryLog implements XaRecoveryLog {

	private RecoveryLog log;

	public DefaultXaRecoveryLog(RecoveryLog log) {
		this.log = log;
	}

	@Override
	public void presumedAborting(XID xid) throws IllegalStateException, LogException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid, TxState.IN_DOUBT);
		log.presumedAborting(entry);
	}

	@Override
	public void terminated(XID xid) {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid, TxState.TERMINATED);
		log.terminated(entry);
	}

	private ParticipantLogEntry convertXidToParticipantLogEntry(XID xid, TxState state) {
		//TODO : description should include unique resource name...
		ParticipantLogEntry entry = new ParticipantLogEntry(
				xid.getGlobalTransactionIdAsString(),
				xid.getBranchQualifierAsString(), 0, xid.toString(),
				state
			);
		return entry;
	}

	@Override
	public Set<XID> getExpiredCommittingXids() throws LogReadException {
		Set<XID> ret = new HashSet<XID>();
		Collection<ParticipantLogEntry> entries = log.getCommittingParticipants();
		for (ParticipantLogEntry entry : entries) {
			if (expired(entry) && !http(entry)) {
				XID xid = new XID(entry.coordinatorId, entry.uri);
				ret.add(xid);
			}
		}
		return ret;
	}

	private boolean http(ParticipantLogEntry entry) {
		return entry.uri.startsWith("http");
	}

	private boolean expired(ParticipantLogEntry entry) {
		long now = System.currentTimeMillis();
		return now > entry.expires;
	}

	@Override
	public void terminatedWithHeuristicHazardByResource(XID xid) throws LogException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid, TxState.HEUR_HAZARD);
		log.terminatedWithHeuristicHazard(entry);
	}

	@Override
	public void terminatedWithHeuristicCommitByResource(XID xid) throws LogException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid, TxState.HEUR_COMMITTED);
		log.terminatedWithHeuristicCommit(entry);
	}

	@Override
	public void terminatedWithHeuristicMixedByResource(XID xid) throws LogException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid, TxState.HEUR_MIXED);
		log.terminatedWithHeuristicMixed(entry);
	}

	@Override
	public void terminatedWithHeuristicRollbackByResource(XID xid) throws LogException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid, TxState.HEUR_ABORTED);
		log.terminatedWithHeuristicRollback(entry);
	}

}
