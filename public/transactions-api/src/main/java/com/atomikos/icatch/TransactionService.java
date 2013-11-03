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

import java.util.Properties;

import com.atomikos.icatch.admin.LogControl;
import com.atomikos.icatch.provider.TransactionServicePlugin;

 /**
  * This internal interface is the base interface for creating transactions.
  * It also acts as a container for existing transactions.
  * Each transaction manager has a transaction service to take care
  * of recovery, and for creating transactions and subtransactions.
  */
  
public interface TransactionService 
{
      /**
       * Adds a listener to the transaction service.
       * This method should be called before init!
       * If called afterwards, only the init (false)
       * callback will happen in the listener.
       *
       * @param listener The listener.
       */
       
      public void addTSListener ( TransactionServicePlugin listener );
      
       /**
        * Removes a listener from the transaction service.
        * @param listener The listener.
        */
        
      public void removeTSListener ( TransactionServicePlugin listener );
  
  
       /**
        * Gets the composite transaction with the given tid.
        * @param tid The transaction identifier.
        * @return CompositeTransaction The transaction, or null if none.
        */
        
      public CompositeTransaction getCompositeTransaction ( String tid );
      
      /**
       * Initializes the TM, and recovers.
       * Should be first method called.
       * @param properties The properties used to 
       * initialize the system.
       * @exception SysException
       */
     
      public void init ( Properties properties ) throws SysException;
      
      /**
       * Starts a new transaction.
       *
       *
       * @timeout Timeout (in millis) after which heuristics are done for indoubts.
       *
       *
       * @return CompositeTransaction The new instance.
       * 
       * @exception SysException 
       *
       */
       
      public CompositeTransaction createCompositeTransaction ( 
                  long timeout )
                  throws SysException;

      
      /**
       * Recreates a composite transaction based on an imported context.
       * Needed by the application's communication layer.
       *
       * @param context The propagation context. Any interposition 
       * actions should already have taken place, so that the propagation
       * is ready to be used by the local transaction service. 
       * @param orphancheck If true, real composite transactions are done. 
       * If false, unchecked behavior applies.
       * 
       * @param heur_commit True for heuristic commit, false for heuristic
       * rollback. 
       * 
       * @param timeout Time in millis after which heur_commit is applied.
       *
       * @return CompositeTransaction The recreated local instance.
       * @exception SysException
       */


      public CompositeTransaction 
      recreateCompositeTransaction (  Propagation context , 
                                       boolean orphancheck ,
                                       boolean heur_commit )
      throws SysException;
        
      /**
       * Shuts down the server in a clean way.
       *
       * @param force If true, shutdown will not wait
       * for possibly indoubt txs to finish.
       * Calling shutdown with force being true implies that 
       * shutdown will not fail, but there may be remaining timer
       * threads that stay asleep until there timeouts expire.
       * Such remaining active transactions will NOT be able to finish,
       * because the recovery manager will be shutdown by that time.
       * New transactions will not be allowed.
       *
       * @exception IllegalStateException If active transactions exist, and not force.
       * @exception SysException
       */
     
      public void shutdown ( boolean force )
      throws SysException, IllegalStateException;
      
       /**
        * Gets a participant handle for the given root.
        * This method is for subordinated coordinators: in those 
        * cases, a participant role is fulfilled w.r.t. parent 
        * coordinators.
        * @param root The root identifier.
        * @return Participant The participant instance.
        * @exception SysException On failure, or if the given root is not known.
        */
      
      public Participant getParticipant ( String root )
      throws SysException;
      
      /**
       * Gets a composite coordinator for the given root.
       * Needed to allow TM to swap out instances after 
       * heuristic termination.
       * If a commit, abort, forget or replay request comes in,
       * this method should be called first to revive the instance.
       *
       * @param root The root in case.
       * @return The composite coordinator for this root.
       * @exception SysException
       */
      
      public CompositeCoordinator getCompositeCoordinator ( String root )
      throws SysException;
      
      /**
       * Gets the superior recovery coordinator for a given root.
       * Needed for imported transactions only. 
       * @param root The root ID
       * @return The recovery coordinator, or null if the root does not exist or if the root was not imported.
       */
      
      public RecoveryCoordinator getSuperiorRecoveryCoordinator ( String root );
      
      /**
       * Gets the logcontrol instance for admin purposes.
       */
      public LogControl getLogControl();
}
