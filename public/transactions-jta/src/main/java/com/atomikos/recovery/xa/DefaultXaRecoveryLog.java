package com.atomikos.recovery.xa;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.xa.Xid;

import com.atomikos.datasource.xa.XID;
import com.atomikos.recovery.ParticipantLogEntry;
import com.atomikos.recovery.RecoveryException;
import com.atomikos.recovery.RecoveryLog;

public class DefaultXaRecoveryLog implements XaRecoveryLog {

	private RecoveryLog log;

	public DefaultXaRecoveryLog(RecoveryLog log) {
		this.log = log;
	}

	@Override
	public void presumedAborting(Xid xid) throws IllegalStateException {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		log.presumedAborting(entry);
	}

	@Override
	public void terminated(Xid xid) {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		log.terminated(entry);
	}

	private ParticipantLogEntry convertXidToParticipantLogEntry(Xid xid) {
		ParticipantLogEntry entry = new ParticipantLogEntry(
				XID.getGlobalTransactionIdAsString(xid),
				XID.getBranchQualifierAsString(xid), 0
			);
		return entry;
	}

	@Override
	public Set<Xid> getExpiredCommittingXids() throws RecoveryException {
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
	public void terminatedWithHeuristicHazardByResource(Xid xid) {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		log.terminatedWithHeuristicHazard(entry);
	}

	@Override
	public void terminatedWithHeuristicCommitByResource(Xid xid) {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		log.terminatedWithHeuristicCommit(entry);
	}

	@Override
	public void terminatedWithHeuristicMixedByResource(Xid xid) {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		log.terminatedWithHeuristicMixed(entry);
	}

	@Override
	public void terminatedWithHeuristicRollbackByResource(Xid xid) {
		ParticipantLogEntry entry = convertXidToParticipantLogEntry(xid);
		log.terminatedWithHeuristicRollback(entry);
	}

}
