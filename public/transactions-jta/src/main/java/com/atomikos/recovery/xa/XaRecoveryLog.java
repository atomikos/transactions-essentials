/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.xa;

import java.util.Set;

import com.atomikos.datasource.xa.XID;
import com.atomikos.recovery.LogException;


public interface XaRecoveryLog {


	/**
	 * Notifies the log that recovery is about to rollback a pending xid according to the "presumed abort" paradigm.
	 * 
	 * This method fails if:
	 * <ul>
	 * <li>The xid is currently COMMITTING in the log, OR</li>
	 * <li>The xid is still PREPARING but not yet expired at the moment this method is called</li>
	 * </ul>
	 * 
	 * @param xid
	 * @throws IllegalStateException If the state precondition is not satisfied.
	 */
	public void presumedAborting(XID xid) throws IllegalStateException, LogException;

	/**
	 * Notifies the log that recovery has terminated a pending xid.
	 * 
	 * Due to concurrent OLTP commit or rollback, the xid may no longer be known in the log. 
	 * In that case, calling this method does nothing.
	 * 
	 * @param xid
	 */
	public void terminated(XID xid);

	public Set<XID> getExpiredCommittingXids() throws LogException;

	public void terminatedWithHeuristicHazardByResource(XID xid) throws LogException;

	public void terminatedWithHeuristicCommitByResource(XID xid) throws LogException;

	public void terminatedWithHeuristicMixedByResource(XID xid) throws LogException;

	public void terminatedWithHeuristicRollbackByResource(XID xid) throws LogException;
}
