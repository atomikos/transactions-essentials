/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.datasource.xa;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.util.Enumeration;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.datasource.ResourceException;
import com.atomikos.datasource.ResourceTransaction;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.DataSerializable;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionControl;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.SerializationUtils;

/**
 * 
 * 
 * An implementation of ResourceTransaction for XA transactions.
 */

public class XAResourceTransaction implements ResourceTransaction,
		Externalizable, Participant, DataSerializable {
	private static final Logger LOGGER = LoggerFactory
			.createLogger(XAResourceTransaction.class);

	static final long serialVersionUID = -8227293322090019196L;

	protected static String interpretErrorCode(String resourceName,
			String opCode, Xid xid, int errorCode) {

		String msg = "unkown";
		switch (errorCode) {
		case XAException.XAER_RMFAIL:
			msg = "the XA resource has become unavailable";
			break;
		case XAException.XA_RBROLLBACK:
			msg = "the XA resource has rolled back for an unspecified reason";
			break;
		case XAException.XA_RBCOMMFAIL:
			msg = "the XA resource rolled back due to a communication failure";
			break;
		case XAException.XA_RBDEADLOCK:
			msg = "the XA resource has rolled back because of a deadlock";
			break;
		case XAException.XA_RBINTEGRITY:
			msg = "the XA resource has rolled back due to a constraint violation";
			break;
		case XAException.XA_RBOTHER:
			msg = "the XA resource has rolled back for an unknown reason";
			break;
		case XAException.XA_RBPROTO:
			msg = "the XA resource has rolled back because it did not expect this command in the current context";
			break;
		case XAException.XA_RBTIMEOUT:
			msg = "the XA resource has rolled back because the transaction took too long";
			break;
		case XAException.XA_RBTRANSIENT:
			msg = "the XA resource has rolled back for a temporary reason - the transaction can be retried later";
			break;
		case XAException.XA_NOMIGRATE:
			msg = "XA resume attempted in a different place from where suspend happened";
			break;
		case XAException.XA_HEURHAZ:
			msg = "the XA resource may have heuristically completed the transaction";
			break;
		case XAException.XA_HEURCOM:
			msg = "the XA resource has heuristically committed";
			break;
		case XAException.XA_HEURRB:
			msg = "the XA resource has heuristically rolled back";
			break;
		case XAException.XA_HEURMIX:
			msg = "the XA resource has heuristically committed some parts and rolled back other parts";
			break;
		case XAException.XA_RETRY:
			msg = "the XA command had no effect and may be retried";
			break;
		case XAException.XA_RDONLY:
			msg = "the XA resource had no updates to perform for this transaction";
			break;
		case XAException.XAER_RMERR:
			msg = "the XA resource detected an internal error";
			break;
		case XAException.XAER_NOTA:
			msg = "the supplied XID is invalid for this XA resource";
			break;
		case XAException.XAER_INVAL:
			msg = "invalid arguments were given for the XA operation";
			break;
		case XAException.XAER_PROTO:
			msg = "the XA resource did not expect this command in the current context";
			break;
		case XAException.XAER_DUPID:
			msg = "the supplied XID already exists in this XA resource";
			break;
		case XAException.XAER_OUTSIDE:
			msg = "the XA resource is currently involved in a local (non-XA) transaction";
			break;
		default:
			msg = "unknown";
		}
		return "XA resource '" + resourceName + "': " + opCode + " for XID '"
				+ xidToHexString(xid) + "' raised " + errorCode + ": " + msg;
	}

	private String tid, root;
	private boolean isXaSuspended;
	private TxState state;
	private String resourcename;
	private transient Xid xid;
	private transient String xidToHexString;
	private transient String toString;

	private void setXid(Xid xid) {
		this.xid = xid;
		this.xidToHexString = xidToHexString(xid);
		this.toString = "XAResourceTransaction: " + this.xidToHexString;
	}

	private transient XATransactionalResource resource;
	private transient XAResource xaresource;
	private transient boolean knownInResource;
	private transient int timeout;

	public XAResourceTransaction() {
		// needed for externalization mechanism
	}

	XAResourceTransaction(XATransactionalResource resource,
			CompositeTransaction transaction, String root) {
		setResource(resource);
		TransactionControl control = transaction.getTransactionControl();
		if (control != null) {
			this.timeout = (int) transaction.getTransactionControl()
					.getTimeout() / 1000;

		}
		this.tid = transaction.getTid();
		this.root = root;
		this.resourcename = resource.getName();
		setXid(this.resource.createXid(this.tid));
		setState(TxState.ACTIVE);
		this.isXaSuspended = false;
		this.knownInResource = false;
	}

	void setResource(XATransactionalResource resource) {
		this.resource = resource;
	}

	void setState(TxState state) {
		this.state = state;
	}

	static String xidToHexString(Xid xid) {
		String gtrid = StringUtils.byteArrayToHexString(xid
				.getGlobalTransactionId());
		String bqual = StringUtils.byteArrayToHexString(xid
				.getBranchQualifier());

		return gtrid + ":" + bqual;
	}

	private void switchToHeuristicState(String opCode, TxState state,
			XAException cause) {
		String errorMsg = interpretErrorCode(this.resourcename, opCode,
				this.xid, cause.errorCode);
		setState(state);
	}

	protected void testOrRefreshXAResourceFor2PC() throws XAException {
		try {

			// fix for case 31209: refresh entire XAConnection on heur hazard
			if (this.state.equals(TxState.HEUR_HAZARD))
				forceRefreshXAConnection();
			else if (this.xaresource != null) { // null if connection failure
				assertConnectionIsStillAlive(); 
			}
		} catch (XAException xa) {
			// timed out?
			if (LOGGER.isDebugEnabled())
				LOGGER.logDebug(this.resourcename
						+ ": XAResource needs refresh", xa);

			if (this.resource == null) {
				// cf bug 67951 - happens on recovery without resource found
				throwXAExceptionForUnavailableResource();
			} else {
				this.xaresource = this.resource.getXAResource();
			}
		}

	}

	private void assertConnectionIsStillAlive() throws XAException {
		this.xaresource.isSameRM(this.xaresource);
	}

	protected void forceRefreshXAConnection() throws XAException {
		if (LOGGER.isDebugEnabled())
			LOGGER.logDebug(this.resourcename
					+ ": forcing refresh of XAConnection...");
		if (this.resource == null) {
			// cf bug 67951 - happens on recovery without resource found
			throwXAExceptionForUnavailableResource();
		}

		try {
			this.xaresource = this.resource.refreshXAConnection();
		} catch (ResourceException re) {
			LOGGER.logWarning(this.resourcename
					+ ": could not refresh XAConnection", re);
		}
	}

	private void throwXAExceptionForUnavailableResource() throws XAException {
		String msg = this.resourcename
				+ ": resource no longer available - recovery might be at risk!";
		LOGGER.logWarning(msg);
		XAException err = new XAException(msg);
		err.errorCode = XAException.XAER_RMFAIL;
		throw err;
	}

	/**
	 * Needed for garbage collection of res tx instances: if no new siblings can
	 * arrive, this method removes any pointers to res txs in the resource
	 */

	private void terminateInResource() {
		if (this.resource != null)
			this.resource.removeSiblingMap(this.root);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(this.xid);
		out.writeObject(this.tid);
		out.writeObject(this.root);
		out.writeObject(this.state);
		out.writeObject(this.resourcename);
		if (this.xaresource instanceof Serializable) {
			// cf case 59238
			out.writeObject(Boolean.TRUE);
			out.writeObject(this.xaresource);
		} else {
			out.writeObject(Boolean.FALSE);
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {

		setXid((Xid) in.readObject());
		this.tid = (String) in.readObject();
		this.root = (String) in.readObject();
		this.state = (TxState) in.readObject();

		this.resourcename = (String) in.readObject();

		try {
			Boolean xaresSerializable = (Boolean) in.readObject();
			if (xaresSerializable != null && xaresSerializable) {
				// cf case 59238
				this.xaresource = (XAResource) in.readObject();
			}
		} catch (OptionalDataException e) {
			// happens if boolean is missing - like in older logfiles
			LOGGER.logDebug("Ignoring missing field", e);
		}

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
				if (LOGGER.isInfoEnabled()) {
					LOGGER.logInfo("XAResource.end ( " + this.xidToHexString
							+ " , XAResource.TMSUCCESS ) on resource "
							+ this.resourcename
							+ " represented by XAResource instance "
							+ this.xaresource);
				}
				this.xaresource.end(this.xid, XAResource.TMSUCCESS);

			} catch (XAException xaerr) {
				String msg = interpretErrorCode(this.resourcename, "end",
						this.xid, xaerr.errorCode);
				if (LOGGER.isDebugEnabled())
					LOGGER.logDebug(msg, xaerr);
				// don't throw: fix for case 102827
			}
			setState(TxState.LOCALLY_DONE);
		}
	}

	boolean supportsTmJoin() {
		return !(this.resource.usesWeakCompare()
				|| this.resource.acceptsAllXAResources() || isActive());
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
			if (LOGGER.isInfoEnabled()) {
				LOGGER.logInfo("XAResource.start ( " + this.xidToHexString
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
	public void setCascadeList(java.util.Dictionary allParticipants)
			throws SysException {
		// nothing to do: local participant
	}

	public Object getState() {
		return this.state;
	}

	/**
	 * @see Participant
	 */
	@Override
	public boolean recover() throws SysException {
		boolean recovered = false;
		// perform extra initialization

		if (beforePrepare()) {
			// see case 23364: recovery before prepare should do nothing
			// and certainly not reset the xaresource
			return false;
		}

		recovered = tryRecoverWithEveryResourceToEnsureOurXidIsNotEndedByPresumedAbort();

		if (!recovered && getXAResource() != null) {
			// cf case 59238: support serializable XAResource
			recovered = true;
		}
		if (recovered)
			this.knownInResource = true;
		return recovered;
	}

	private boolean beforePrepare() {
		return TxState.ACTIVE.equals(this.state)
				|| TxState.LOCALLY_DONE.equals(this.state);
	}

	/**
	 * Recovered XIDs can be shared in two resources if they connect to the same
	 * back-end RM (remember: we use the TM name for the branch!) So each
	 * resource needs to know that our Xid can be recovered, or endRecovery in
	 * one of them will incorrectly rollback.
	 * 
	 * @return True iff at least one resource was found that recovers this
	 *         instance.
	 */
	private boolean tryRecoverWithEveryResourceToEnsureOurXidIsNotEndedByPresumedAbort() {
		boolean ret = false;
		Enumeration resources = Configuration.getResources();
		while (resources.hasMoreElements()) {
			RecoverableResource res = (RecoverableResource) resources
					.nextElement();
			if (res.recover(this)) {
				ret = true;
			}

		}
		return ret;
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
			LOGGER.logDebug("Error forgetting xid: " + this.xid, err);
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

		if (TxState.ACTIVE.equals(this.state)) {
			// tolerate non-delisting apps/servers
			suspend();
		}

		// duplicate prepares can happen for siblings in serial subtxs!!!
		// in that case, the second prepare just returns READONLY
		if (this.state.equals(TxState.IN_DOUBT))
			return Participant.READ_ONLY;
		else if (!this.state.equals(TxState.LOCALLY_DONE))
			throw new SysException("Wrong state for prepare: " + this.state);
		try {
			// refresh xaresource for MQSeries: seems to close XAResource after
			// suspend???
			testOrRefreshXAResourceFor2PC();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.logDebug("About to call prepare on XAResource instance: "
						+ this.xaresource);
			}
			ret = this.xaresource.prepare(this.xid);

		} catch (XAException xaerr) {
			String msg = interpretErrorCode(this.resourcename, "prepare",
					this.xid, xaerr.errorCode);
			LOGGER.logWarning(msg, xaerr); // see case 84253
			if (XAException.XA_RBBASE <= xaerr.errorCode
					&& xaerr.errorCode <= XAException.XA_RBEND) {
				throw new RollbackException(msg);
			} else {
				throw new SysException(msg, xaerr);
			}
		}
		setState(TxState.IN_DOUBT);
		if (ret == XAResource.XA_RDONLY) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.logInfo("XAResource.prepare ( " + this.xidToHexString
						+ " ) returning XAResource.XA_RDONLY " + "on resource "
						+ this.resourcename
						+ " represented by XAResource instance "
						+ this.xaresource);
			}
			return Participant.READ_ONLY;
		} else {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.logInfo("XAResource.prepare ( " + this.xidToHexString
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

		if (this.state.equals(TxState.HEUR_MIXED))
			throw new HeurMixedException();
		if (this.state.equals(TxState.HEUR_COMMITTED))
			throw new HeurCommitException();
		if (this.xaresource == null) { // if recover failed
			LOGGER.logWarning("XAResourceTransaction "
					+ getXid()
					+ ": no XAResource to rollback - the required resource is probably not yet intialized?");
			throw new HeurHazardException();
		}

		try {
			if (this.state.equals(TxState.ACTIVE)) { // first suspend xid
				suspend();
			}

			// refresh xaresource for MQSeries: seems to close XAResource after
			// suspend???
			testOrRefreshXAResourceFor2PC();
			if (LOGGER.isInfoEnabled()) {
				LOGGER.logInfo("XAResource.rollback ( " + this.xidToHexString
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
					&& xaerr.errorCode <= XAException.XA_RBEND) { // do nothing,
																	// corresponds
																	// with
																	// semantics
																	// of
																	// rollback
				if (LOGGER.isDebugEnabled())
					LOGGER.logDebug(msg);
			} else {
				LOGGER.logWarning(msg, xaerr);
				switch (xaerr.errorCode) {
				case XAException.XA_HEURHAZ:
					switchToHeuristicState("rollback", TxState.HEUR_HAZARD,
							xaerr);
					throw new HeurHazardException();
				case XAException.XA_HEURMIX:
					switchToHeuristicState("rollback", TxState.HEUR_MIXED,
							xaerr);
					throw new HeurMixedException();
				case XAException.XA_HEURCOM:
					switchToHeuristicState("rollback", TxState.HEUR_COMMITTED,
							xaerr);
					throw new HeurCommitException();
				case XAException.XA_HEURRB:
					forget();
					break;
				case XAException.XAER_NOTA:
					// see case 21552
					if (LOGGER.isDebugEnabled()) {
						LOGGER.logDebug("XAResource.rollback: invalid Xid - already rolled back in resource?");
					}
					setState(TxState.TERMINATED);
					// ignore error - corresponds to semantics of rollback!
					break;
				default:
					// fix for bug 31209
					switchToHeuristicState("rollback", TxState.HEUR_HAZARD,
							xaerr);
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

		if (this.state.equals(TxState.TERMINATED))
			return;
		if (this.state.equals(TxState.HEUR_MIXED))
			throw new HeurMixedException();
		if (this.state.equals(TxState.HEUR_ABORTED))
			throw new HeurRollbackException();
		if (this.xaresource == null) { // null if recovery failed
			LOGGER.logWarning("XAResourceTransaction "
					+ getXid()
					+ ": no XAResource to commit - the required resource is probably not yet intialized?");
			throw new HeurHazardException();
		}

		try {

			if (TxState.ACTIVE.equals(this.state)) { // tolerate non-delisting
														// apps/servers
				suspend();
			}
		} catch (ResourceException re) {
			// happens if already rolled back or something else;
			// in any case the transaction can be trusted to act
			// as if rollback already happened
			throw new com.atomikos.icatch.RollbackException(re.getMessage());
		}

		if (!(this.state.equals(TxState.LOCALLY_DONE)
				|| this.state.equals(TxState.IN_DOUBT) || this.state
					.equals(TxState.HEUR_HAZARD)))
			throw new SysException("Wrong state for commit: " + this.state);
		try {
			// refresh xaresource for MQSeries: seems to close XAResource after
			// suspend???
			testOrRefreshXAResourceFor2PC();
			if (LOGGER.isInfoEnabled()) {
				LOGGER.logInfo("XAResource.commit ( " + this.xidToHexString
						+ " , " + onePhase + " ) on resource "
						+ this.resourcename
						+ " represented by XAResource instance "
						+ this.xaresource);
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
							"Already rolled back in resource.");
			} else {
				switch (xaerr.errorCode) {
				case XAException.XA_HEURHAZ:
					switchToHeuristicState("commit", TxState.HEUR_HAZARD, xaerr);
					throw new HeurHazardException();
				case XAException.XA_HEURMIX:
					switchToHeuristicState("commit", TxState.HEUR_MIXED, xaerr);
					throw new HeurMixedException();
				case XAException.XA_HEURCOM:
					forget();
					break;
				case XAException.XA_HEURRB:
					switchToHeuristicState("commit", TxState.HEUR_ABORTED,
							xaerr);
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
					switchToHeuristicState("commit", TxState.HEUR_HAZARD, xaerr);
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
		return this.xidToHexString.hashCode();
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

	protected void setRecoveredXAResource(XAResource xaresource) {
		// See case 25671: only reset xaresource if NOT enlisted!
		// Otherwise, the delist will fail since XA does not allow
		// enlist/delist on different xaresource instances.
		// This should not interfere with recovery since a recovered
		// instance will NOT have state ACTIVE...
		if (!TxState.ACTIVE.equals(this.state)) {
			setXAResource(xaresource);
		}
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
		if (LOGGER.isDebugEnabled())
			LOGGER.logDebug(this + ": about to switch to XAResource "
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
		if (LOGGER.isDebugEnabled())
			LOGGER.logDebug("XAResourceTransaction " + getXid()
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
				if (LOGGER.isInfoEnabled()) {
					LOGGER.logInfo("XAResource.suspend ( "
							+ this.xidToHexString
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
			if (LOGGER.isInfoEnabled()) {
				LOGGER.logInfo("XAResource.start ( " + this.xidToHexString
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
		return XID.getBranchQualifierAsString(xid);
	}

	String getResourceName() {
		return this.resourcename;
	}

	XAResource getXAResource() {
		return this.xaresource;
	}

	@Override
	public void writeData(DataOutput out) throws IOException {
		byte[] data = SerializationUtils.serialize((Serializable) this.xid);
		out.writeInt(data.length);
		out.write(data);
		out.writeUTF(this.tid);
		out.writeUTF(this.root);
		out.writeUTF(this.state.toString());
		out.writeUTF(this.resourcename);
		if (this.xaresource instanceof Serializable) {
			// cf case 59238
			out.writeBoolean(true);
			byte[] bytes = SerializationUtils
					.serialize((Serializable) this.xaresource);
			out.writeInt(bytes.length);
			out.write(bytes);
		} else {
			out.writeBoolean(false);
		}

	}

	@Override
	public void readData(DataInput in) throws IOException {
		// xid_ ???

		// String branchQualifier = in.readUTF();
		int len = in.readInt();
		byte[] data = new byte[len];
		in.readFully(data);
		this.xid = SerializationUtils.deserialize(data);

		this.tid = in.readUTF();
		setXid(this.xid);

		this.root = in.readUTF();
		this.state = TxState.valueOf(in.readUTF());
		this.resourcename = in.readUTF();
		boolean xaresourceSerializable = in.readBoolean();
		if (xaresourceSerializable) {
			int size = in.readInt();
			byte[] bytes = new byte[size];
			in.readFully(bytes);
			this.xaresource = SerializationUtils.deserialize(bytes);
		}
	}

}
