/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;


/**
 * Represents a handle to import a transaction from an incoming request, 
 * so that the work in this VM becomes part of the request's commit or rollback.
 */
 
 public interface ImportingTransactionManager
 {
    /**
     * Imports the transaction propagation obtained from an incoming request.
     *
     * @param propagation The ancestor information.
     * @param orphancheck True if orphans are to be checked.
     * @param heur_commit True if heuristic means commit.
     *
     * @return CompositeTransaction The locally created transaction instance that takes part in the global commit/rollback.
     * This instance will also be mapped to the calling thread.
     */
     
     CompositeTransaction 
      importTransaction ( Propagation propagation , 
                          boolean orphancheck , boolean heur_commit 
                         ) throws SysException;
    
  
                          
    /**
     * Signals that the incoming request is done processing, in order to
     * terminate the transaction context for the calling thread.
     * 
     * @param commit True if the invocation had no errors: commit the local transaction
     * but make its final outcome subject to the request's commit/rollback.
     *
     * @return Extent The extent to return to remote client.
     * 
     * 
     * @exception RollbackException If no transaction exists, e.g. if it has been rolled back already.
     * 
     * @exception SysException
     * 
     */
     
    Extent terminated( boolean commit ) 
    throws SysException, RollbackException;

    
 
    

 }
