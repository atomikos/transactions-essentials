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
 * A control for a transaction.
 * This groups the methods that are only available to 
 * priviledged ( usually meaning local ) application code.
 *
 * @deprecated As from release 3.0, the methods of this interface have been 
 * moved to the basic CompositeTransaction interface.
 *
 */


 public interface TransactionControl
 extends java.io.Serializable
 {
        
    /**
     *Create a subtx for this transaction.
     *
     *@return CompositeTransaction The subtx.
     *@exception IllegalStateException If no longer active.
     */

    public CompositeTransaction createSubTransaction()
        throws SysException,
	     IllegalStateException;
	     
    /**
     *Set serial mode for root.
     *This only works on the root itself, and can not be undone.
     *After this, no parallel calls are allowed in any descendant.
     *@exception IllegalStateException If  called for non-root tx.
     *@exception SysException For unexpected errors.
     */

    public void setSerial() throws IllegalStateException, SysException;

	     
    /**
     *Get a terminator for this tx.
     *
     *@return CompositeTerminator A terminator, null if none.
     */

    public CompositeTerminator getTerminator();
    
    /**
     *Get the number of subtxs that were locally started for this
     *instance.
     *@return int The number of locally started subtxs.
     */
     
     public int getLocalSubTxCount();
     
    /**
     *Sets the tag for this transaction. This is returned as a summary of
     *the local work in case the transaction was imported from a remote
     *client TM.
     *
     *@param tag The tag to add to the transaction.
     */
     
    public void setTag ( HeuristicMessage tag ) ;


    /**
     *Get the extent for the transaction.
     */

     public Extent getExtent();
     
     
      /**
       *Get the timeout in ms.
       *
       *@return long The timeout, in ms, of the tx.
       */
       
     public long getTimeout();
	
	 /**
	  *Marks the transaction so that the only possible
	  *termination is rollback. 
	  *
	  */
	
	 public void setRollbackOnly();
 }
