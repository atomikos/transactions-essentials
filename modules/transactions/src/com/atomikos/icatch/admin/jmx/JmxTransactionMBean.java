package com.atomikos.icatch.admin.jmx;

/**
 * 
 * 
 * 
 * 
 * 
 * An MBean interface for administration of pending transactions.
 */

public interface JmxTransactionMBean
{

    /**
     * Get the transaction identifier.
     * 
     * @return String The unique id.
     */

    public String getTid ();

    /**
     * Get the transaction's state.
     * 
     * @return String The state, represented as by its name
     */

    public String getState ();

    /**
     * Get the high-level heuristic comments. This is what remote clients will
     * see as well.
     * 
     * @return HeuristicMessage The comments giving a summary of the tasks done
     *         in this transaction.
     */

    public String[] getTags ();

    /**
     * Get the HeuristicMessage detailed info for this transaction.
     * 
     * @return HeuristicMessage[] The detailed heuristic messages. These show
     *         the comments for EACH individual resource that was part of the
     *         transaction.
     */

    public String[] getHeuristicMessages ();

}
