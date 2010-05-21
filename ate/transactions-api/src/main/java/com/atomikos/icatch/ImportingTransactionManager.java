package com.atomikos.icatch;

/**
 *
 *
 *An interface for the communication layer, for notifying TM of 
 *incoming transactional request.
 *
 *<b>
 *WARNING: this interface and its mechanisms are subject to several patents and
 *pending patents held by Atomikos. Regardless the license
 *under which this interface is distributed, third-party use is 
 *NOT allowed without the prior and explicit 
 *written approval of Atomikos.
 *</b>
 */
 
 public interface ImportingTransactionManager
 {
    /**
     *Notify TM of incoming request with given propagation.
     *Makes the TM start a tx and associate it with calling 
     *thread.
     *
     *@param propagation The ancestor information.
     *@param orphancheck True iff orphans are to be checked.
     *@param heur_commit True iff heuristic means commit.
     *
     *@return CompositeTransaction The local tx instance.
     */
     
    public CompositeTransaction 
      importTransaction ( Propagation propagation , 
                          boolean orphancheck , boolean heur_commit 
                         ) throws SysException;
    
  
                          
    /**
     *Termination callback for current tx. 
     *Called by comm layer right before
     *a remote call returns. 
     *@param commit True iff the invocation had no errors.
     *Implies that the local subtx is committed.
     *
     *@return Extent The extent to return to remote client.
     *@exception SysException Unexpected error.
     *@exception RollbackException If the transaction has timed out.
     */
     
    public Extent terminated( boolean commit ) 
    throws SysException, RollbackException;
   // , HeurRollbackException,
//    HeurMixedException, HeurHazardException;

    
 
    

 }
