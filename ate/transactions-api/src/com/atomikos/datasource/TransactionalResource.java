//$Id: TransactionalResource.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: TransactionalResource.java,v $
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
//Revision 1.6  2005/08/09 15:25:28  guy
//Updated javadoc.
//
//Revision 1.5  2004/10/12 13:04:48  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.4  2002/02/26 14:08:20  guy
//Corrected getResourceTransaction: IllegalStateException if CT finished.
//Required for JTA compatibility.
//
//Revision 1.3  2002/01/07 10:49:12  guy
//Moved close method to RecoverableResource, where it belongs.
//
//Revision 1.2  2001/11/30 13:29:47  guy
//Updated files: UniqueId changed to String.
//
//Revision 1.5  2001/02/26 19:36:38  pardon
//Redesign to OTS.
//
//Revision 1.4  2001/02/25 11:13:40  pardon
//Added a lot.
//
//Revision 1.3  2001/02/21 10:08:00  pardon
//Added only needed files.
//
//Revision 1.1  2001/02/19 18:23:13  pardon
//Added new interfaces and classes for redesign.
//

package com.atomikos.datasource;

import com.atomikos.icatch.CompositeTransaction;

/**
 *
 *
 *A Transactional Resource is the abstraction of a data source that
 *supports transactions and recovery.
 */

public interface TransactionalResource extends RecoverableResource
{
    /**
     *Get or create a ResourceTransaction. This instructs the resource
     *to internally start a context for a new transaction.
     *If the resource decides to return a new instance, it should
     *also make sure that before returning, the new resource 
     *transaction is registered as a participant for the current
     *composite transaction.
     *
     *
     *@param ct The composite transaction for whom this is done.
     *This serves as a handle for the resource to determine isolation 
     *properties, coordinator reference and so on.
     *For instance, XA implementations can use the isSameTransaction() 
     *function to determine if an existing XID should be used to 
     *start the internal XAResource before passing it on as a
     *ResourceTransaction (wrapped) instance.
     *
     *@return ResourceTransaction a handle to the new context.
     *
     *@exception IllegalStateException If the given transaction is no longer
     *active.
     *@exception ResourceException On failure.
     */

    public ResourceTransaction 
        getResourceTransaction ( CompositeTransaction ct ) 
        throws IllegalStateException, ResourceException;


//    
//    /**
//     *Gets the state recovery manager for this resource, if any.
//     *Only resources that support compensation will have a 
//     *state recovery manager. This method allows external inspection
//     *of the compensatable tx states.
//     *
//     *@return StateRecoveryManager The state recovery manager, or null if none.
//     *For regular XA based resources, this is likely to return null.
//     */
//     
//    public StateRecoveryManager getRecoveryManager()
//        throws ResourceException;
// 
//    /**
//     *Close the resource manager for shutdown.
//     *This notifies the resource manager that it is no longer needed.
//     */
//
//    public void close() throws ResourceException;
    
 
    
 
    ///**
//     *Test if a resource is the same as another one.
//     */
//
//    public boolean isSameRM(TransactionalResource res) 
//        throws ResourceException;

}
