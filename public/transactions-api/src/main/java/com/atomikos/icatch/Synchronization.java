/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;

import com.atomikos.recovery.TxState;



 /**
  * A synchronization inferface for transaction termination callbacks.
  * Instances are volatile, i.e. not recovered after a crash/restart.
  */
  
 public interface Synchronization 
 extends java.io.Serializable 
 {
    /**
     * Called before prepare decision is made.
     */
     
     void beforeCompletion ();
    
    /**
     * Called after the overall outcome  is known.
     *
     * @param txstate The state of the coordinator after preparing.
     * Equals either null ( readonly ), TxState.COMMITTING  or TxState.ABORTING.
     */
     
     void afterCompletion ( TxState txstate );	
 }
