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
import com.atomikos.datasource.xa.XID;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.LogException;

public class XaResourceRecoveryManager {

	private static final Logger LOGGER = LoggerFactory.createLogger(XaResourceRecoveryManager.class);
	private XaRecoveryLog log;

	private XidSelector xidSelector;

	private boolean autoForget = true;
	
	private XaResourceRecoveryManager(XaRecoveryLog log, final String tmUniqueName) {
		this.log=log;
		this.xidSelector=new XidSelector() {
			@Override
			public boolean selects(Xid vendorXid) {
				boolean ret = false;
				String branch = new String ( vendorXid.getBranchQualifier () );
				Xid xid = wrapWithOurOwnXidToHaveCorrectEqualsAndHashCode ( vendorXid );
                if ( branch.startsWith ( tmUniqueName ) ) {
                	ret = true;
                    if(LOGGER.isInfoEnabled()){
                    	LOGGER.logInfo("Resource " + tmUniqueName + " recovering XID: " + xid);
                    }
                } else {
                	if(LOGGER.isInfoEnabled()){
                		LOGGER.logInfo("Resource " + tmUniqueName + ": XID " + xid + 
                		" with branch " + branch + " is not under my responsibility");
                	}
                }
                return ret;
			}

			private Xid wrapWithOurOwnXidToHaveCorrectEqualsAndHashCode(Xid xid) {
				return new XID(xid);
			}						
		}; 
	}
	
	

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
		} catch (LogException couldNotRetrieveCommittingXids) {
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
				handleHeuristicTerminationByResource(xid, xaResource, e);
			} else if (xidTerminatedInResourceByConcurrentCommit(e)) {
				log.terminated(xid);
			} else {
				// temporary: retry later
				// TODO log warning
			}
		}
	}

	private void handleHeuristicTerminationByResource(Xid xid,
			XAResource xaResource, XAException e) {
		try {
			notifyLogOfHeuristic(xid, e);
			forgetXidInXaResourceIfAllowed(xid, xaResource);
		} catch (LogException transientLogWriteException) {
			LOGGER.logWarning("Failed to log heuristic termination of Xid: "+xid+" - ignoring to retry later", transientLogWriteException);
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

	private Set<Xid> retrieveExpiredCommittingXidsFromLog() throws LogException {
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
					handleHeuristicTerminationByResource(xid, xaResource, e);
				} else if (xidTerminatedInResourceByConcurrentRollback(e)) {
					log.terminated(xid);
				} else {
					LOGGER.logWarning("Unexpected exception during recovery - ignoring to retry later", e);
				}
			}
		} catch (IllegalStateException presumedAbortNotAllowedInCurrentLogState) {
			// ignore to retry later if necessary
		} catch (LogException logWriteException) {
			LOGGER.logWarning("log write failed for Xid: "+xid+", ignoring to retry later", logWriteException);
		}
	}

	private void notifyLogOfHeuristic(Xid xid, XAException e) throws LogException {
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
	
	private static XaResourceRecoveryManager instance;

	public static XaResourceRecoveryManager getInstance() {
		return instance;
	}
	
	public static void installXaResourceRecoveryManager(XaRecoveryLog xaRecoveryLog, String tmUniqueName) {
		if (xaRecoveryLog == null) {
			instance = null;
		} else {
			instance = new XaResourceRecoveryManager(xaRecoveryLog, tmUniqueName);
			
		}
	}

	
}
