package com.atomikos.recovery.xa;

import java.util.Set;

import javax.transaction.xa.Xid;


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
	public void presumedAborting(Xid xid) throws IllegalStateException;

	/**
	 * Notifies the log that recovery has terminated a pending xid.
	 * 
	 * Due to concurrent OLTP commit or rollback, the xid may no longer be known in the log. 
	 * In that case, calling this method does nothing.
	 * 
	 * @param xid
	 */
	public void terminated(Xid xid);

	public Set<Xid> getExpiredCommittingXids();

	public void terminatedWithHeuristicHazardByResource(Xid xid);

	public void terminatedWithHeuristicCommitByResource(Xid xid);

	public void terminatedWithHeuristicMixedByResource(Xid xid);

	public void terminatedWithHeuristicRollbackByResource(Xid xid);
}
