package com.atomikos.icatch;

/**
 *
 *
 *An interface for a TM that allows outgoing remote calls to be 
 *transactional.
 *
 *<b>
 *WARNING: this interface and its mechanisms are subject to several patents and
 *pending patents held by Atomikos. Regardless the license
 *under which this interface is distributed, third-party use is 
 *NOT allowed without the prior and explicit 
 *written approval of Atomikos.
 *</b>
 *
 */
 
 public interface ExportingTransactionManager 
 {
    /** 
     *Get the propagation info of the tx for the calling thread.
     *Called before doing the remote call.
     *
     *@return Propagation The propagation for the current thread.
     *
     *@exception SysException If no tx for current thread.
     *@exception RollbackException If the current transaction
     *has already rolled back.
     */
     
    
    public Propagation getPropagation () 
    throws SysException, RollbackException;
    
    /**
     *Called after call returns successfully:
     *add the extent of the call to the
     *current tx.
     *If a remote call has failed, this method should NOT be called.
     *
     *@param extent The extent of the call.
     *@exception RollbackException If the current transaction
     *has already rolled back.
     *@exception SysException On failure.
     */
     
     
    public void addExtent ( Extent extent ) 
    throws SysException, RollbackException;
 }
