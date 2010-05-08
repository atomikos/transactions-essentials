package com.atomikos.icatch;

/**
 * 
 * 
 * An interface for transactional threads. Implementing this interface allows
 * you to start multithreaded subtransactions that run as subtransactions of
 * the calling thread's transaction. Implementations do NOT need to create,
 * start or commit/rollback these subtransactions: all management is done by the
 * system.
 * 
 * NOTE: in case of a top-level transaction in preferred serial mode, the
 * subtransactional threads will be serialized with respect to each other.
 * 
 */

public interface SubTxCode
{
    /**
     * The method that contains the subtransactional logic. Before this method
     * is called, the system will have started a subtransaction, and associated
     * it with the thread that will execute exec(). If this method exits without
     * an exception, then the subtransaction will be committed.
     * 
     * @exception Exception
     *                On failure. In that case, the corresponding subtransaction
     *                will be rolled back by the system.
     */

    public void exec () throws Exception;
}
