//$Id: ExportingTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: ExportingTransactionManager.java,v $
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
//Revision 1.5  2005/08/09 15:23:38  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.4  2005/08/08 11:23:27  guy
//Added RollbackException to ExportingTM interface.
//Added public constructor for CompositeTransactionAdaptor.
//
//Revision 1.3  2005/08/05 15:03:28  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.2  2004/10/12 13:03:25  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//

package com.atomikos.icatch;

/**
 *
 *
 *An interface for a TM that allows outgoing remote calls to be 
 *transactional.
 *
 *<b>
 *WARNING: this interface and its mechanisms are subject to several patents and
 *pending patents held by Atomikos. Regardless the license
 *under which this interface is distributed, third-party use is 
 *NOT allowed without the prior and explicit 
 *written approval of Atomikos.
 *</b>
 *
 */
 
 public interface ExportingTransactionManager 
 {
    /** 
     *Get the propagation info of the tx for the calling thread.
     *Called before doing the remote call.
     *
     *@return Propagation The propagation for the current thread.
     *
     *@exception SysException If no tx for current thread.
     *@exception RollbackException If the current transaction
     *has already rolled back.
     */
     
    
    public Propagation getPropagation () 
    throws SysException, RollbackException;
    
    /**
     *Called after call returns successfully:
     *add the extent of the call to the
     *current tx.
     *If a remote call has failed, this method should NOT be called.
     *
     *@param extent The extent of the call.
     *@exception RollbackException If the current transaction
     *has already rolled back.
     *@exception SysException On failure.
     */
     
     
    public void addExtent ( Extent extent ) 
    throws SysException, RollbackException;
 }
