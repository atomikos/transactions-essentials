//$Id: Propagation.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: Propagation.java,v $
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
//Revision 1.2  2006/03/21 13:23:48  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:22  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/08/09 15:23:39  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.3  2005/08/05 15:03:28  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.2  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//

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
