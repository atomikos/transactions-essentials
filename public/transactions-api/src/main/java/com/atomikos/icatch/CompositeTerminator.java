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
 *A handle to terminate the composite transaction.
 *Must ALWAYS be used to handle termination throughout the system,
 *also for subtransactions!
 *
 *
 */

public interface CompositeTerminator 
{
    /**
     *Commit the composite transaction.
     *
     *@exception HeurRollbackException On heuristic rollback.
     *@exception HeurMixedException On heuristic mixed outcome.
     *@exception SysException For unexpected failures.
     *@exception SecurityException If calling thread does not have 
     *right to commit.
     *@exception HeurHazardException In case of heuristic hazard.
     *@exception RollbackException If the transaction was rolled back
     *before prepare.
     */

    public void commit() 
        throws 
	  HeurRollbackException,HeurMixedException,
	  HeurHazardException,
	  SysException,java.lang.SecurityException,
	  RollbackException;



    /**
     *Rollback the current transaction.
     *@exception IllegalStateException If prepared or inactive.
     *@exception SysException If unexpected error.
     */

    public void rollback()
        throws IllegalStateException, SysException;
}
