/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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
