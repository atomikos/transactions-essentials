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

package com.atomikos.icatch.system;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import com.atomikos.icatch.imp.thread.InterruptedExceptionHelper;

/**
 * A class for synchronizing on subtransaction threads. You need this
 * functionality because a subtransaction thread has to finish before the
 * creator transaction is done. A new waiter instance can be used to create <a
 * href="SubTxThread.html">SubTxThread</a> instances.
 */

public class Waiter
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(Waiter.class);

    private int numActive;
    // the number of still active threads.
    private int abortCount;
    // the number of aborted threads after waitForAll returns.
    private boolean noneActive;

    // for serial mode: true if a subtx is active.

    public Waiter ()
    {
        numActive = 0;
        abortCount = 0;
        noneActive = true;
    }
    
    //for testing
    synchronized int getNumActive() 
    {
    		return numActive;
    }

    synchronized void getToken () throws InterruptedException
    {
        while ( !noneActive )
            wait ();
        noneActive = false;
    }

    synchronized void giveToken ()
    {
        noneActive = true;
        notifyAll ();
    }

    synchronized void incActives ()
    {
        numActive++;
    }

    /**
     * Waits until all active threads for this object are done.
     */
    public synchronized void waitForAll ()
    {
        try {
            while ( numActive > 0 )
                wait ();
        } catch ( InterruptedException e ) {
        	// cf bug 67457
			InterruptedExceptionHelper.handleInterruptedException ( e );
        }
    }

    synchronized void incAbortCount ()
    {
        abortCount++;
    }

    synchronized void decActives ()
    {
        numActive--;
        if ( numActive == 0 )
            notifyAll ();
    }

    /**
     * After waitForAll has returned, this method gives the number of threads
     * that exited with an exception. Subtransactions of failed threads have
     * been rolled back, and subtransactions of succeeded threads have been
     * subtransaction-committed (and included in the 2PC set of the parent
     * transaction).
     */

    public int getAbortCount ()
    {
        return abortCount;
    }

}
