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
 *A composite transaction manager. This interface
 *outlines the API for managing composite transactions
 *in the local VM.
 */
 
 public interface CompositeTransactionManager
 {
    /**
     *Starts a new (sub)transaction (not an activity) for the current thread.
     *Associates the current thread with that instance.
     *<br>
     *<b>NOTE:</b> subtransactions should not be mixed: either each subtransaction is
     *an activity, or not (default). Use suspend/resume if mixed models are necessary:
     *for instance, if you want to create a normal transaction within an activity, then
     *suspend the activity first before starting the transaction. Afterwards, resume the
     *activity.
     *
     *@timeout Timeout ( in ms ) for the transaction.
     *
     *@return CompositeTransaction The new instance.
     *@exception SysException Unexpected error.
     *@exception IllegalStateException If there is an existing transaction that is 
     *an activity instead of a classical transaction. 
     *
     */

    public CompositeTransaction createCompositeTransaction ( 
                                                             long timeout ) 
        throws SysException, IllegalStateException;
    
    /**
     *Gets the composite transaction for the current thread.
     *
     *@return CompositeTransaction The instance for the current thread, null if none.
     *
     *@exception SysException Unexpected failure.
     */

      public CompositeTransaction getCompositeTransaction () throws SysException
;
      
       /**
        *Gets the composite transaction with the given id.
        *This method is useful e.g. for retrieving a suspended 
        *transaction by its id.
        *
        *@param tid The id of the transaction.
        *@return CompositeTransaction The transaction with the given id,
        *or null if not found.
        *@exception SysException Unexpected failure.
        */
        
      public CompositeTransaction getCompositeTransaction ( String tid )
      throws SysException;
      
    /**
     *Re-maps the thread to the given tx.
     *
     *@param ct The CompositeTransaction to resume.
     *@exception IllegalStateException If thread has tx already.
     *@exception SysException Unexpected failure.
     */
     
      public void resume ( CompositeTransaction ct )
        throws IllegalStateException, SysException;
        
    /**
     *Suspends the tx for the current thread.
     *
     *@return CompositeTransaction The transaction for the current thread.
     *
     *@exception SysException On failure.
     */

      public CompositeTransaction suspend() throws SysException ;
//      
//      /**
//       * Starts a new transaction with the option of making it an activity.
//       * Activities are treated differently: they are recoverable even while active,
//       * and can be longer-running and compensation-based. 
//       * @param timeout The timeout in ms.
//       * @param activity True if the instance needs to be an activity.
//       * @return The instance.
//       * @throws SysException Unexpected error.
//       * @throws IllegalStateException If an incompatible transaction already exists 
//       * (activities should not be mixed with other transactions).
//       */
//      
//      public CompositeTransaction createCompositeTransaction ( long timeout , boolean activity )
//      throws SysException, IllegalStateException;


 }
