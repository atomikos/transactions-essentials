package com.atomikos.icatch.system;

/**
 * 
 * 
 * A class for synchronizing on subtransaction threads. You need this
 * functionality because a subtransaction thread has to finish before the
 * creator transaction is done. A new waiter instance can be used to create <a
 * href="SubTxThread.html">SubTxThread</a> instances.
 */

public class Waiter
{
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
