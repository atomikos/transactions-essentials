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







