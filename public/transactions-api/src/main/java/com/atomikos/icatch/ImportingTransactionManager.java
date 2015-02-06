/**
 * Copyright (C) 2000-2011 Atomikos <info@atomikos.com>
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
     
    public CompositeTransaction 
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
     
    public Extent terminated( boolean commit ) 
    throws SysException, RollbackException;

    
 
    

 }
