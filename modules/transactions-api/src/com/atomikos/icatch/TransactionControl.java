//$Id: TransactionControl.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: TransactionControl.java,v $
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
//Revision 1.1.1.1  2006/03/09 14:59:22  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.11  2005/08/23 13:06:34  guy
//Updated SOAP init parameters.
//Moved CommitProtocol to msg package.
//
//Revision 1.10  2005/08/09 15:23:39  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.9  2005/08/05 15:03:29  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.8  2004/10/25 08:45:56  guy
//Updated TODOs
//
//Revision 1.7  2004/10/12 13:03:27  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.6  2004/03/22 15:36:53  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.5.10.4  2004/01/14 10:38:25  guy
//Corrected forget to not block.
//
//Revision 1.5.10.3  2003/09/11 13:42:08  guy
//Applied STATE pattern to CompositeTransactionImp.
//
//Revision 1.5.10.2  2003/09/10 14:00:47  guy
//Added rollback only support in kernel; corrected bug: subtx allowed addParticipant after commit.
//
//Revision 1.5.10.1  2003/06/20 16:31:33  guy
//*** empty log message ***
//
//Revision 1.5  2002/03/03 11:19:58  guy
//Adapted to setTag which is now in TransactionControl.
//Also getState is only in CompositeTransaction, no longer in TransactionControl.
//
//Revision 1.4  2001/10/30 16:00:35  guy
//Added getTimeout method to TransactionControl; needed for OTS version.
//
//Revision 1.3  2001/10/30 13:40:15  guy
//Added getState method to TransactionControl. Needed for OTS layer.
//
//Revision 1.2  2001/10/28 16:04:53  guy
//Split TM functionality in two parts: one for managing roots and txs,
//and one for mapping these to threads.
//Introduction of the TransactionService interface and implementations.
//These changes were best for implementing JTS.
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//

package com.atomikos.icatch;

/**
 *
 *A control for a transaction.
 *This groups the methods that are only available to 
 *priviledged ( usually meaning local ) application code.
 *
 *@deprecated As from release 3.0, the methods of this interface have been 
 *moved to the basic CompositeTransaction interface.
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
