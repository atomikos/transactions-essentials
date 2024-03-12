/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa;

import java.util.Map;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.atomikos.datasource.ResourceException;
import com.atomikos.datasource.ResourceTransaction;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.TxState;
import com.atomikos.util.Assert;

/**
 * 
 * 
 * An implementation of ResourceTransaction for XA transactions.
 */

public class XAResourceTransaction implements ResourceTransaction, Participant {
	private static final Logger LOGGER = LoggerFactory.createLogger(XAResourceTransaction.class);

	static final long serialVersionUID = -8227293322090019196L;

	private static String interpretErrorCode(String resourceName,
			String opCode, Xid xid, int errorCode) {

		String msg = XAExceptionHelper.convertErrorCodeToVerboseMessage(errorCode);
		return "XA resource '" + resourceName + "': " + opCode + " for XID '"
				+ xid + "' raised " + errorCode + ": " + msg;
	}


	private String tid, root;
	private boolean isXaSuspended;
	private TxState state;
	private String resourcename;
	private transient XID xid;
	
	private transient String toString;

	private void setXid(XID xid) {
		this.xid = xid;
		this.toString = "XAResourceTransaction: " + xid;
	}

	private transient final XATransactionalResource resource;
	private transient XAResource xaresource;
	private transient boolean knownInResource;
	private transient int timeout;


	XAResourceTransaction(XATransactionalResource resource,
			CompositeTransaction transaction, String root) {
		Assert.notNull("resource cannot be null", resource);
		this.resource=resource;
		this.timeout = (int) transaction.getTimeout() / 1000;

		this.tid = transaction.getCompositeCoordinator().getCoordinatorId(); // cf case 162083
		this.root = root;
		this.resourcename = resource.getName();
		setXid(this.resource.createXid(this.tid));
		setState(TxState.ACTIVE);
		this.isXaSuspended = false;
		this.knownInResource = false;
	}

	

	void setState(TxState state) {
		if (state.isHeuristic()) {
			LOGGER.logWarning("Heuristic termination of " + toString() + " with state " + state);
		}
		this.state = state;
	}



	

	protected void testOrRefreshXAResourceFor2PC() throws XAException {
		try {
			// fix for case 31209: refresh entire XAConnection on heur hazard
			if (state == TxState.HEUR_HAZARD)
				forceRefreshXAConnection();
			else if (xaresource != null) { // null if connection failure
				assertConnectionIsStillAlive(); 
			}
		} catch (XAException xa) {
			// timed out?
			if (LOGGER.isTraceEnabled()) {
				LOGGER.logTrace(this.resourcename + ": XAResource needs refresh?", xa);
			}
		}
		if (xaresource == null) {
			xaresource = resource.getXAResource();
		}
	}

	private void assertConnectionIsStillAlive() throws XAException {
		this.xaresource.isSameRM(this.xaresource);
	}

	private void forceRefreshXAConnection() throws XAException {
		if (LOGGER.isTraceEnabled())
			LOGGER.logTrace(this.resourcename
					+ ": forcing refresh of XAConnection...");

		try {
			this.xaresource = this.resource.refreshXAConnection();
		} catch (ResourceException re) {
			LOGGER.logWarning(this.resourcename
					+ ": could not refresh XAConnection", re);
		}
	}

	/**
	 * Needed for garbage collection of res tx instances: if no new siblings can
	 * arrive, this method removes any pointers to res txs in the resource
	 */

	private void terminateInResource() {
		if (this.resource != null)
			this.resource.removeSiblingMap(this.root);
	}

	/**
	 * @see ResourceTransaction.
	 */
	public String getTid() {
		return this.tid;
	}

	/**
	 * @see ResourceTransaction.
	 */

	@Override
	public synchronized void suspend() throws ResourceException {

		// BugzID: 20545
		// State may be IN_DOUBT or TERMINATED when a connection is closed AFTER
		// commit!
		// In that case, don't call END again, and also don't generate any
		// error!
		// This is required for some hibernate connection release strategies.
		if (this.state.equals(TxState.ACTIVE)) {
			try {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.logDebug("XAResource.end ( " + xid
							+ " , XAResource.TMSUCCESS ) on resource "
							+ this.resourcename
							+ " represented by XAResource instance "
							+ this.xaresource);
				}
				this.xaresource.end(this.xid, XAResource.TMSUCCESS);

			} catch (XAException xaerr) {
				String msg = interpretErrorCode(this.resourcename, "end",
						this.xid, xaerr.errorCode);
				if (LOGGER.isTraceEnabled())
					LOGGER.logTrace(msg, xaerr);
				// don't throw: fix for case 102827
			}
			setState(TxState.LOCALLY_DONE);
		}
	}

	boolean supportsTmJoin() {
		return !(isActive());
	}

	/**
	 * @see ResourceTransaction.
	 */

	@Override
	public synchronized void resume() throws ResourceException {
		int flag = 0;
		String logFlag = "";
		if (this.state.equals(TxState.LOCALLY_DONE)) {// reused instance
			flag = XAResource.TMJOIN;
			logFlag = "XAResource.TMJOIN";
		} else if (!this.knownInResource) {// new instance
			flag = XAResource.TMNOFLAGS;
			logFlag = "XAResource.TMNOFLAGS";
		} else
			throw new IllegalStateException("Wrong state for resume: "
					+ this.state);

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.logDebug("XAResource.start ( " + xid
						+ " , " + logFlag + " ) on resource "
						+ this.resourcename
						+ " represented by XAResource instance "
						+ this.xaresource);
			}
			this.xaresource.start(this.xid, flag);

		} catch (XAException xaerr) {
			String msg = interpretErrorCode(this.resourcename, "resume",
					this.xid, xaerr.errorCode);
			LOGGER.logWarning(msg, xaerr);
			throw new ResourceException(msg, xaerr);
		}
		setState(TxState.ACTIVE);
		this.knownInResource = true;
	}

	/**
	 * @see Participant
	 */

	@Override
	public void setCascadeList(Map<String, Integer> allParticipants)
			throws SysException {
		// nothing to do: local participant
	}

	public Object getState() {
		return this.state;
	}

	private boolean beforePrepare() {
		return TxState.ACTIVE.equals(this.state)
				|| TxState.LOCALLY_DONE.equals(this.state);
	}

	/**
	 * @see Participant
	 */

	@Override
	public void setGlobalSiblingCount(int count) {
		// nothing to be done here
	}

	/**
	 * @see Participant
	 */

	@Override
	public synchronized void forget() {
		terminateInResource();
		try {
			if (this.xaresource != null) { // null if recovery failed
				this.xaresource.forget(this.xid);
			}
		} catch (Exception err) {
			LOGGER.logTrace("Error forgetting xid: " + this.xid, err);
			// we don't care here
		}
		setState(TxState.TERMINATED);
	}

	/**
	 * @see Participant
	 */

	@Override
	public synchronized int prepare() throws RollbackException,
			HeurHazardException, HeurMixedException, SysException {
		int ret = 0;
		terminateInResource();

		if (TxState.ACTIVE == this.state) {
			// tolerate non-delisting apps/servers
			suspend();
		}

		// duplicate prepares can happen for siblings in serial subtxs!!!
		// in that case, the second prepare just returns READONLY
		if (this.state == TxState.IN_DOUBT)
			return Participant.READ_ONLY;
		else if (!(this.state == TxState.LOCALLY_DONE))
			throw new SysException("Wrong state for prepare: " + this.state);
		try {
			// refresh xaresource for MQSeries: seems to close XAResource after
			// suspend???
			testOrRefreshXAResourceFor2PC();
			if (LOGGER.isTraceEnabled()) {
				LOGGER.logTrace("About to call prepare on XAResource instance: "
						+ this.xaresource);
			}
			ret = this.xaresource.prepare(this.xid);

		} catch (XAException xaerr) {
			String msg = interpretErrorCode(this.resourcename, "prepare",
					this.xid, xaerr.errorCode);
			if (XAException.XA_RBBASE <= xaerr.errorCode
					&& xaerr.errorCode <= XAException.XA_RBEND) {
				LOGGER.logWarning(msg, xaerr); // see case 84253
				throw new RollbackException(msg, xaerr);
			} else {
				LOGGER.logError(msg, xaerr);
				throw new SysException(msg, xaerr);
			}
		}
		setState(TxState.IN_DOUBT);
		if (ret == XAResource.XA_RDONLY) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.logDebug("XAResource.prepare ( " + xid
						+ " ) returning XAResource.XA_RDONLY " + "on resource "
						+ this.resourcename
						+ " represented by XAResource instance "
						+ this.xaresource);
			}
			return Participant.READ_ONLY;
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.logDebug("XAResource.prepare ( " + xid
						+ " ) returning OK " + "on resource "
						+ this.resourcename
						+ " represented by XAResource instance "
						+ this.xaresource);
			}
			return Participant.READ_ONLY + 1;
		}
	}

	/**
	 * @see Participant.
	 */

	@Override
	public synchronized void rollback()
			throws HeurCommitException, HeurMixedException,
			HeurHazardException, SysException {
		terminateInResource();

		if (rollbackShouldDoNothing()) {
			return;
		}
		if (this.state.equals(TxState.TERMINATED)) {
			return;
		}

		if (this.state.equals(TxState.HEUR_MIXED)) {
			throw new HeurMixedException();			
		}
		if (this.state.equals(TxState.HEUR_COMMITTED)) {
			throw new HeurCommitException();
		}

		try {
			if (this.state.equals(TxState.ACTIVE)) { // first suspend xid
				suspend();
			}

			// refresh xaresource for MQSeries: seems to close XAResource after
			// suspend???
			testOrRefreshXAResourceFor2PC();
			if (this.xaresource == null) { 
				throw new HeurHazardException("XAResourceTransaction "
						+ getXid() + ": no XAResource to rollback?");
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.logDebug("XAResource.rollback ( " + xid
						+ " ) " + "on resource " + this.resourcename
						+ " represented by XAResource instance "
						+ this.xaresource);
			}
			this.xaresource.rollback(this.xid);

		} catch (ResourceException resErr) {
			// failure of suspend
			throw new SysException("Error in rollback: " + resErr.getMessage(),
					resErr);
		} catch (XAException xaerr) {
			String msg = interpretErrorCode(this.resourcename, "rollback",
					this.xid, xaerr.errorCode);
			if (XAException.XA_RBBASE <= xaerr.errorCode
					&& xaerr.errorCode <= XAException.XA_RBEND) { 
				if (LOGGER.isTraceEnabled())
					LOGGER.logTrace(msg);
			} else {
				LOGGER.logWarning(msg, xaerr);
				switch (xaerr.errorCode) {
				case XAException.XA_HEURHAZ:
					setState(TxState.HEUR_HAZARD);
					throw new HeurHazardException();
				case XAException.XA_HEURMIX:
					setState(TxState.HEUR_MIXED);
					throw new HeurMixedException();
				case XAException.XA_HEURCOM:
					setState(TxState.HEUR_COMMITTED);
					throw new HeurCommitException();
				case XAException.XA_HEURRB:
					forget();
					break;
				case XAException.XAER_NOTA:
					// see case 21552
					if (LOGGER.isTraceEnabled()) {
						LOGGER.logTrace("XAResource.rollback: invalid Xid - already rolled back in resource?");
					}
					setState(TxState.TERMINATED);
					// ignore error - corresponds to semantics of rollback!
					break;
				default:
					// fix for bug 31209
					setState(TxState.HEUR_HAZARD);
					throw new SysException(msg, xaerr);
				}
			}
		}
		setState(TxState.TERMINATED);
	}

	private boolean rollbackShouldDoNothing() {
		return !this.knownInResource && beforePrepare();
	}

	/**
	 * @see Participant
	 */

	@Override
	public synchronized void commit(boolean onePhase)
			throws HeurRollbackException, HeurHazardException,
			HeurMixedException, RollbackException, SysException {
		
		terminateInResource();
		
		if (this.state.equals(TxState.TERMINATED)) {
			return;
		}
		if (this.state.equals(TxState.HEUR_MIXED)) {
			throw new HeurMixedException();
		}
		if (this.state.equals(TxState.HEUR_ABORTED)) {		
			throw new HeurRollbackException();
		}
		if (!(this.state.isOneOf(TxState.LOCALLY_DONE, TxState.IN_DOUBT, TxState.HEUR_HAZARD))) {
			throw new SysException("Wrong state for commit: " + this.state);
		}
		
		if (onePhase) {
			if (xaresource == null) {
				throw new com.atomikos.icatch.RollbackException(toString() + ": no XAResource to commit");
			}
			try {
				if (TxState.ACTIVE.equals(state)) { 
					// tolerate non-delisting apps/servers
					suspend();
				}
			} catch (ResourceException re) {
				// happens if already rolled back or something else;
				// in any case the transaction can be trusted to act
				// as if rollback already happened
				throw new com.atomikos.icatch.RollbackException(re.getMessage());
			}
		} 

		
		try {
			// refresh xaresource for MQSeries: seems to close XAResource after suspend???
			if (!onePhase) { // cf case 167209
				testOrRefreshXAResourceFor2PC();
				if (xaresource == null) {
					String msg =  toString + ": no XAResource to commit - recovery will handle this in the background...";
					LOGGER.logWarning(msg);
					throw new HeurHazardException(msg);
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.logDebug("XAResource.commit ( " + xid
						+ " , " + onePhase + " ) on resource " + this.resourcename + 
						" represented by XAResource instance " + this.xaresource);
			}
			this.xaresource.commit(this.xid, onePhase);

		} catch (XAException xaerr) {
			String msg = interpretErrorCode(this.resourcename, "commit",
					this.xid, xaerr.errorCode);
			LOGGER.logWarning(msg, xaerr);

			if (XAException.XA_RBBASE <= xaerr.errorCode
					&& xaerr.errorCode <= XAException.XA_RBEND) {

				if (!onePhase)
					throw new SysException(msg, xaerr);
				else
					throw new com.atomikos.icatch.RollbackException(
							"Already rolled back in resource.", xaerr);
			} else {
				switch (xaerr.errorCode) {
				case XAException.XA_HEURHAZ:
					setState(TxState.HEUR_HAZARD);
					throw new HeurHazardException();
				case XAException.XA_HEURMIX:
					setState(TxState.HEUR_MIXED);
					throw new HeurMixedException();
				case XAException.XA_HEURCOM:
					forget();
					break;
				case XAException.XA_HEURRB:
					setState(TxState.HEUR_ABORTED);
					throw new HeurRollbackException();
				case XAException.XAER_NOTA:
					if (!onePhase) {
						// see case 21552
						LOGGER.logWarning("XAResource.commit: invalid Xid - transaction already committed in resource?");
						setState(TxState.TERMINATED);
						break;
					}
				default:
					// fix for bug 31209
					setState(TxState.HEUR_HAZARD);
					throw new SysException(msg, xaerr);
				}
			}
		}
		setState(TxState.TERMINATED);
	}

	/**
	 * Absolutely necessary for coordinator to work correctly
	 */

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		// NOTE: basing equals on the xid means that if two
		// different instances for the same xid exist then these
		// will be considered the same, and a second will
		// NOT be added to the coordinator's participant list.
		// However, this is not a problem, since the first added instance
		// will do all commit work (they are equivalent).
		// Note that this can ONLY happen for two invocations of the
		// SAME local composite transaction to the SAME resource.
		// Because INSIDE ONE local transaction there is no
		// internal parallellism, having two invocations to the same
		// resource execute with the same xid is not a problem.

		if (!(o instanceof XAResourceTransaction))
			return false;

		XAResourceTransaction other = (XAResourceTransaction) o;
		return this.xid.equals(other.xid);
	}

	/**
	 * Absolutely necessary for coordinator to work correctly
	 */

	@Override
	public int hashCode() {
		return xid.hashCode();
	}

	@Override
	public String toString() {
		
		return this.toString;
	}

	/**
	 * Get the Xid. Needed by jta mappings.
	 * 
	 * @return Xid The Xid of this restx.
	 */

	public Xid getXid() {
		return this.xid;
	}

	/**
	 * Set the XAResource attribute.
	 * 
	 * @param xaresource
	 *            The new XAResource to use. This new XAResource represents the
	 *            new connection to the XA database. Necessary because on reuse,
	 *            the old xaresource may be in use by another thread, for
	 *            another transaction.
	 */

	public void setXAResource(XAResource xaresource) {
		if (LOGGER.isTraceEnabled())
			LOGGER.logTrace(this + ": about to switch to XAResource "
					+ xaresource);
		this.xaresource = xaresource;
		try {
			this.xaresource.setTransactionTimeout(this.timeout);
		} catch (XAException e) {
			String msg = interpretErrorCode(this.resourcename,
					"setTransactionTimeout", this.xid, e.errorCode);
			LOGGER.logWarning(msg, e);
			// we don't care
		}
		if (LOGGER.isTraceEnabled())
			LOGGER.logTrace("XAResourceTransaction " + getXid()
					+ ": switched to XAResource " + xaresource);
	}

	/**
	 * Perform an XA suspend.
	 */

	public void xaSuspend() throws XAException {
		// cf case 61305: make XA suspend idempotent so appserver suspends do
		// not interfere with our suspends (triggered by transaction suspend)
		if (!this.isXaSuspended) {
			try {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.logDebug("XAResource.suspend ( "
							+ xid
							+ " , XAResource.TMSUSPEND ) on resource "
							+ this.resourcename
							+ " represented by XAResource instance "
							+ this.xaresource);
				}
				this.xaresource.end(this.xid, XAResource.TMSUSPEND);

				this.isXaSuspended = true;
			} catch (XAException xaerr) {
				String msg = interpretErrorCode(this.resourcename, "suspend",
						this.xid, xaerr.errorCode);
				LOGGER.logWarning(msg, xaerr);
				throw xaerr;
			}
		}
	}

	/**
	 * Perform an xa resume
	 */

	public void xaResume() throws XAException {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.logDebug("XAResource.start ( " + xid
						+ " , XAResource.TMRESUME ) on resource "
						+ this.resourcename
						+ " represented by XAResource instance "
						+ this.xaresource);
			}
			this.xaresource.start(this.xid, XAResource.TMRESUME);

			this.isXaSuspended = false;
		} catch (XAException xaerr) {
			String msg = interpretErrorCode(this.resourcename, "resume",
					this.xid, xaerr.errorCode);
			LOGGER.logWarning(msg, xaerr);
			throw xaerr;
		}

	}

	/**
	 * Test if the resource has been ended with TMSUSPEND.
	 * 
	 * @return boolean True if so.
	 */
	public boolean isXaSuspended() {
		return this.isXaSuspended;
	}

	/**
	 * Test if the restx is active (in use).
	 * 
	 * @return boolean True if so.
	 */
	public boolean isActive() {
		return this.state.equals(TxState.ACTIVE);
	}

	/**
	 * @see com.atomikos.icatch.Participant#getURI()
	 */
	@Override
	public String getURI() {
		return xid.getBranchQualifierAsString();
	}

	public String getResourceName() {
		return this.resourcename;
	}


}
