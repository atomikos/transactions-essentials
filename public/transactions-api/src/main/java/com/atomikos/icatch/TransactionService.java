/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;


 /**
  * This internal interface is the base interface for creating transactions.
  * It also acts as a container for existing transactions.
  * Each transaction manager has a transaction service to take care
  * of recovery, and for creating transactions and subtransactions.
  * 
  * Application code should not use this interface directly.
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
       
       void addTSListener ( TransactionServicePlugin listener );
      
       /**
        * Removes a listener from the transaction service.
        * @param listener The listener.
        */
        
       void removeTSListener ( TransactionServicePlugin listener );
  
  
       /**
        * Gets the composite transaction with the given tid.
        * @param tid The transaction identifier.
        * @return CompositeTransaction The transaction, or null if none.
        */
        
       CompositeTransaction getCompositeTransaction ( String tid );
      

      
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
       
       CompositeTransaction createCompositeTransaction ( 
                  long timeout )
                  throws SysException;

      
      /**
       * Recreates a composite transaction based on an imported context.
       * Needed by the application's communication layer.
       *
       * @param context The propagation context. Any interposition 
       * actions should already have taken place, so that the propagation
       * is ready to be used by the local transaction service. 
       * 
       * @param timeout Time in millis after which heur_commit is applied.
       *
       * @return CompositeTransaction The recreated local instance.
       * throws SysException
       */


      CompositeTransaction 
      recreateCompositeTransaction ( Propagation context)
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
     
      void shutdown ( boolean force )
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
      
      Participant getParticipant ( String root )
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
      
      CompositeCoordinator getCompositeCoordinator ( String root )
      throws SysException;
      
      /**
       * Called when the transaction is suspended.
       */
      
      void transactionSuspended(CompositeTransaction ct);
      
      /**
       * Called when the transaction is resumed.
       */
      void transactionResumed(CompositeTransaction ct);
      
}
