package com.atomikos.icatch.admin.jmx;

/**
 * 
 * 
 * 
 * 
 * 
 * An MBean for heuristic pending transactions.
 */

public interface JmxHeuristicTransactionMBean extends JmxTransactionMBean
{

    /**
     * Test if the transaction's 2PC outcome was commit. Needed especially for
     * the heuristic states, if the desired outcome (instead of the actual
     * state) needs to be retrieved. For instance, if the state is
     * STATE_HEUR_HAZARD then extra information is needed for determining if the
     * desired outcome was commit or rollback. This method helps here.
     * 
     * 
     * @return boolean True iff commit decided (either heuristically or by the
     *         super coordinator.
     */

    public boolean getCommitted ();

    /**
     * Force the system to forget about the transaction.
     */

    public void forceForget ();

}
