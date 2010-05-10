//$Id: TransactionService.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: TransactionService.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:07  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
//
//Revision 1.2  2006/04/14 12:45:21  guy
//Added properties to TSListener init callback.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:57  guy
//Import.
//
//Revision 1.2  2006/03/21 13:23:48  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:22  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.6  2005/11/01 09:08:56  guy
//Added method: getSuperiorRecoveryCoordinator (needed for WSAT: address only known at prepare time).
//
//Revision 1.5  2005/08/10 16:23:03  guy
//Debugged/adapted for compensation and dito testing.
//
//Revision 1.4  2005/08/05 15:03:29  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.3  2004/10/12 13:03:27  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2001/10/29 16:38:08  guy
//Changed UniqueId for String.
//
//Revision 1.1  2001/10/28 16:07:46  guy
//These are new incorporations of the coordinator and tx management.
//For creating and containing such objects, these provide a reusable
//framework.
//

package com.atomikos.icatch;
import java.util.Properties;

 /**
  *
  *
  *This internal interface is the base interface for creating transactions.
  *It also acts as a container for exising transactions.
  *Each transaction manager has a transaction service to take care
  *of recovery, and for creating transactions and subtransactions.
  */
  
public interface TransactionService 
{
      /**
       *Adds a listener to the transaction service.
       *This method should be called before init!
       *If called afterwards, only the init (false)
       *callback will happen in the listener.
       *
       *@param listener The listener.
       */
       
      public void addTSListener ( TSListener listener );
      
       /**
        *Removes a listener from the transaction service.
        *@param listener The listener.
        */
        
      public void removeTSListener ( TSListener listener );
  
  
       /**
        *Get the composite transaction with the given tid.
        *@param tid The transaction identifier.
        *@return CompositeTransaction The transaction, or null if none.
        */
        
      public CompositeTransaction getCompositeTransaction ( String tid );
      
      /**
       *Initialize TM, and recover.
       *Should be first method called.
       *@param properties The properties used to 
       *initialize the system.
       *@exception SysException Unexpected failure.
       */
     
      public void init ( Properties properties ) throws SysException;
      
      /**
       *Start a new transaction.
       *
       *
       *@timeout Timeout ( in ms ) after which heuristics are done for indoubts.
       *
       *
       *@return CompositeTransaction The new instance.
       *@exception SysException Unexpected error.
       *
       */
       
      public CompositeTransaction createCompositeTransaction ( 
                  long timeout )
                  throws SysException;
      
//      /**
//       * Starts a new transaction with the option of making it an activity.
//       * @param timeout The timeout in ms.
//       * @param activity True if the instance should be an activity.
//       * @return The instance.
//       * @throws SysException Unexpected error.
//       */
//      
//      public CompositeTransaction createCompositeTransaction ( long timeout , boolean activity )
//      throws SysException;
      
      /**
       *Recreate a composite transaction based on an imported context.
       *Needed by the application's communication layer.
       *
       *@param context The propagationcontext. Any interposition 
       *actions should already have taken place, so that the propagation
       *is ready to be used by the local transaction service. 
       *@param orphancheck If true, real composite txs are done. 
       *If false, OTS like behavior applies.
       *@param heur_commit True for heuristic commit, false for heuristic
       *rollback. 
       *@param timeout Time in ms after which heur_commit is applied.
       *
       *@return CompositeTransaction The recreated local instance.
       *@exception SysException Failure.
       */


      public CompositeTransaction 
      recreateCompositeTransaction (  Propagation context , 
                                       boolean orphancheck ,
                                       boolean heur_commit )
      throws SysException;
        
      /**
       *Shut down the server in a clean way.
       *
       *@param force If true, shutdown will not wait
       *for possibly indoubt txs to finish.
       *Calling shutdown with force being true implies that 
       *shutdown will not fail, but there may be remaining timer
       *threads that stay asleep until there timeouts expire.
       *Such remaining active transactions will NOT be able to finish,
       *because the recovery manager will be shutdown by that time.
       *New transactions will not be allowed.
       *
       *@exception SysException For unexpected errors.
       *@exception IllegalStateException If active txs exist, and not force.
       */
     
      public void shutdown ( boolean force )
      throws SysException, IllegalStateException;
      
       /**
        *Get a participant for the given root.
        *This method is for subordinator coordinators: in those 
        *cases, a participant role is fulfilled w.r.t. parent 
        *coordinators.
        *@param root The root identifier.
        *@return Participant The participant instance.
        *@exception SysException On failure, or if the given root is not known.
        */
      
      public Participant getParticipant ( String root )
      throws SysException;
      
      /**
       *Gets a composite coordinator for the given root.
       *Needed to allow TM to swap out instances after 
       *heuristic termination.
       *If a commit, abort, forget or replay request comes in,
       *this method should be called first to revive the instance.
       *
       *@param root The root in case.
       *@return CoordinatorImp The composite coordinator for root.
       *@exception SysException If not found.
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
}
