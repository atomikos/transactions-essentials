//$Id: CompositeCoordinator.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: CompositeCoordinator.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:07  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
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
//Revision 1.4  2006/03/21 16:14:34  guy
//Added setter for active recovery.
//
//Revision 1.3  2006/03/21 14:12:02  guy
//Replaced UnavailableException with UnsupportedOperationException.
//
//Revision 1.2  2006/03/21 13:23:48  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:22  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.6  2005/08/09 15:23:38  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.5  2005/08/09 04:53:08  guy
//Corrected javadoc comment.
//
//Revision 1.4  2005/08/05 15:03:27  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.3  2004/10/12 13:03:25  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2001/10/29 16:38:07  guy
//Changed UniqueId for String.
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//


package com.atomikos.icatch;

/**
 *
 *
 *A CompositeCoordinator represents the per-server work done
 *as part of the same termination scope.
 *
 *
 */

public interface CompositeCoordinator extends java.io.Serializable 
{

    /**
     *Get the identifier for this coordinator.
     *
     *@return String The coordinator id.
     *For imported transactions, this will be the
     *id of the top-level or root transaction. For 
     *subtransactions, this will be an independent id.
     */

    public String getCoordinatorId();
   

    /**
     *Get the recovery coordinator instance for this one.
     *
     *@return RecoveryCoordinator The recovery coordinator.
     */

    public RecoveryCoordinator getRecoveryCoordinator();
    
    /**
     *Get the tags that were set for this coordinator.
     *
     *@return HeuristicMessage[] The tags set.
     *These serve as a summary of the local work
     *towards remote client TMs (that way, these
     *do not have to see ALL local heuristic messages,
     *but rather get a relevant summary).
     *The participant proxy for a local coordinator
     *returns these tags as its heuristic messages.
     */
     
    public HeuristicMessage[] getTags();
    
    
    /**
     * Checks whether the instance is recoverable
     * in the active state. Although active recoverability requires 
     * more logging overhead, some protocols may need this capability.
     * 
     * @return Boolean True iff recoverable in active state.
     * Null if this information is not available (for imported instances).
     * By default, this is false. 
     */
   public Boolean isRecoverableWhileActive();
   
   /**
    * Sets this coordinator to be recoverable
    * while active. Ideally, this method is called
    * before any participants are added 
    * (otherwise, some participants may have been added
    * and not recovered due to an intermediate crash).
    *
    * This operation may not be unavailable for imported 
    * coordinators, but it should always work for 
    * locally created (sub)transactions.
    *
    * <br>
    * <b>NOTE: active recoverability is inherited by 
    * any subtransactions that are created afterwards.</b>
    */
   
   public void setRecoverableWhileActive()
   throws UnsupportedOperationException;
    
    
}







