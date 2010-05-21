/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
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
