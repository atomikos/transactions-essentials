/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;


/**
 * An interface for a TM that allows outgoing remote calls to be transactional.
 */
 
 public interface ExportingTransactionManager 
 {
    /** 
     * Gets the propagation info of the transaction for the calling thread.
     * Should be called before doing the remote call.
     *
     * @return Propagation The propagation for the current thread's transaction.
     *
     * @throws IllegalStateException If no such transaction exists, e.g. after a prior rollback.
     *
     */
    
    Propagation getPropagation() throws SysException, IllegalStateException;
    
    /**
     * Should be called after call returns successfully:
     * adds the extent of the call to the current transaction.
     *
     * If a remote call has failed, this method should NOT be called.
     *
     * @param extent The extent of the call.
     * 
     * @throws IllegalArgumentException If the format of the supplied extent is not recognized.
     * @throws RollbackException If the current transaction has already rolled back.
     */
     
     
    void addExtent(Extent extent) throws SysException, IllegalArgumentException, RollbackException;
 }
