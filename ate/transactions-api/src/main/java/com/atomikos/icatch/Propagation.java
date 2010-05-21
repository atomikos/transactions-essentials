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
