package com.atomikos.recovery.xa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.atomikos.datasource.xa.RecoveryScan;
import com.atomikos.datasource.xa.RecoveryScan.XidSelector;
import com.atomikos.recovery.LogReadException;

public class XaResourceRecoveryManager {

	private XaRecoveryLog log;

	private XidSelector xidSelector;

	private boolean autoForget = true;

	public void recover(XAResource xaResource) {
		List<Xid> xidsToRecover = retrievePreparedXidsFromXaResource(xaResource);
		Collection<Xid> xidsToCommit;
		try {
			xidsToCommit = retrieveExpiredCommittingXidsFromLog();
			for (Xid xid : xidsToRecover) {
				if (xidsToCommit.contains(xid)) {
					replayCommit(xid, xaResource);
				} else {
					attemptPresumedAbort(xid, xaResource);
				}
			}
		} catch (LogReadException couldNotRetrieveCommittingXids) {
			//TODO log warning and retry later
		}
	}

	private void replayCommit(Xid xid, XAResource xaResource) {
		try {
			xaResource.commit(xid, false);
			log.terminated(xid);
		} catch (XAException e) {
			// TODO log error
			if (alreadyHeuristicallyTerminatedByResource(e)) {
				notifyLogOfHeuristic(xid, e);
				forgetXidInXaResourceIfAllowed(xid, xaResource);
			} else if (xidTerminatedInResourceByConcurrentCommit(e)) {
				log.terminated(xid);
			} else {
				// temporary: retry later
				// TODO log warning
			}
		}
	}

	private boolean xidTerminatedInResourceByConcurrentRollback(XAException e) {
		return xidNoLongerKnownByResource(e);
	}

	private boolean alreadyHeuristicallyTerminatedByResource(XAException e) {
		boolean ret = false;
		switch (e.errorCode) {
		case XAException.XA_HEURHAZ:
		case XAException.XA_HEURCOM:
		case XAException.XA_HEURMIX:
		case XAException.XA_HEURRB:
			ret = true;
		}
		return ret;
	}

	private boolean xidTerminatedInResourceByConcurrentCommit(XAException e) {
		return xidNoLongerKnownByResource(e);
	}

	private boolean xidNoLongerKnownByResource(XAException e) {
		// TODO check with latest 3.9 code
		boolean ret = false;
		switch (e.errorCode) {
		case XAException.XAER_NOTA:
		case XAException.XAER_INVAL:
			ret = true;
		}
		return ret;
	}

	private void forgetXidInXaResourceIfAllowed(Xid xid, XAResource xaResource) {
		try {
			if (autoForget)
				xaResource.forget(xid);
		} catch (XAException e) {
			// TODO: log
			// ignore: worst case, heuristic xid is presented again on next
			// recovery scan
		}
	}

	private Set<Xid> retrieveExpiredCommittingXidsFromLog() throws LogReadException {
		return log.getExpiredCommittingXids();
	}

	private List<Xid> retrievePreparedXidsFromXaResource(XAResource xaResource) {
		// TODO wrapWithOurOwnXidToHaveCorrectEqualsAndHashCode(Xid vendorXid)
		List<Xid> ret = new ArrayList<Xid>();
		try {
			ret = RecoveryScan.recoverXids(xaResource, xidSelector);
		} catch (XAException e) {
			// TODO log or publish warning
			// ignore: retry on next recovery pass
		}
		return ret;
	}

	private void attemptPresumedAbort(Xid xid, XAResource xaResource) {
		try {
			log.presumedAborting(xid);
			try {
				xaResource.rollback(xid);
				log.terminated(xid); // TODO add coordinator ID as parameter for
										// fast log update? better: parse Xid to
										// get TID :-)
			} catch (XAException e) {
				if (alreadyHeuristicallyTerminatedByResource(e)) {
					notifyLogOfHeuristic(xid, e);
					forgetXidInXaResourceIfAllowed(xid, xaResource);
				} else if (xidTerminatedInResourceByConcurrentRollback(e)) {
					log.terminated(xid);
				} else {
					// temporary: retry later
					// TODO log warning
				}
			}
		} catch (IllegalStateException presumedAbortNotAllowedInCurrentLogState) {
			// ignore
		}
	}

	private void notifyLogOfHeuristic(Xid xid, XAException e) {
		switch (e.errorCode) {
		case XAException.XA_HEURHAZ:
			log.terminatedWithHeuristicHazardByResource(xid);
			break;
		case XAException.XA_HEURCOM:
			log.terminatedWithHeuristicCommitByResource(xid);
			break;
		case XAException.XA_HEURMIX:
			log.terminatedWithHeuristicMixedByResource(xid);
			break;
		case XAException.XA_HEURRB:
			log.terminatedWithHeuristicRollbackByResource(xid);
			break;
		default:
			break;
		}
	}

	public void setXaRecoveryLog(XaRecoveryLog log) {
		this.log = log;
	}

	public void setXidSelector(XidSelector xidSelector) {
		this.xidSelector = xidSelector;

	}
	public void setAutoForgetHeuristicsOnRecovery(boolean value) {
		autoForget  = value;
	}

}
