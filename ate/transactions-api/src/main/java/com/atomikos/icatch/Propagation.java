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
import java.util.Stack;

/**
 *
 *
 *Information to propagate a transaction to a remote server.
 *
 *
 *
 */

public interface Propagation extends java.io.Serializable 
{
    /**
     *Get the ancestor information as a stack.
     *
     *@return Stack The ancestor transactions.
     */

    public Stack getLineage();
    
    /**
     *Test if serial mode or not; if serial, no parallelism allowed.
     *
     *@return boolean True iff serial mode is set.
     */
    
    public boolean isSerial();
    
    
     /**
      *Get the timeout left for the composite transaction.
      *
      *@return long The time left before timeout.
      */
      
    public long getTimeOut();
    
//    /**
//     * Tests if the transaction should be an activity or not. 
//     * Note that this implies that either 
//     * ALL transactions in a propagation are activities 
//     * or NONE is. This makes sense because the
//     * propagation delimits the SCOPE of the distributed
//     * transaction, and this scope is either longer-lived 
//     * (activity) or 
//     * not, but never both. The scope inherently determines the
//     * moment at which termination is done via two-phase commit.
//     * 
//     * @return boolean True iff recoverable while active.
//     * 
//     */
//    public boolean isActivity();
}
