//$Id: RecoverableResource.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: RecoverableResource.java,v $
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
//Revision 1.1.1.1  2006/03/09 14:59:21  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.7  2004/11/08 15:05:38  guy
//Added javadoc comments.
//
//Revision 1.6  2004/10/12 13:04:48  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.5  2004/09/17 16:14:11  guy
//Added method: isClosed().
//
//Revision 1.4  2004/09/06 09:28:03  guy
//Added setRecoveryService method, to enable recovery after restart.
//
//Revision 1.3  2002/01/07 10:49:11  guy
//Moved close method to RecoverableResource, where it belongs.
//
//Revision 1.2  2001/11/30 13:29:47  guy
//Updated files: UniqueId changed to String.
//

package com.atomikos.datasource;

import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryService;


 /**
  *
  *
  *A Recoverable Resource is the abstraction of a resource 
  *that supports recoverable work (i.e., that supports Participant
  *instances). Its primary use is for Participant instances that are
  *not self-containing, as for instance in XA transactions
  *(where the Participant can not contain the entire transaction
  *context of the server, but only an external reference to it in the
  *form of an Xid instance).
  *A recoverable resource is invoked at recovery time by its
  *own Participant instances (typically in the readExternal 
  *method), who iteratively ask each resource 
  *in the com.atomikos.icatch.Configuration whether or not they 
  *can be recovered by them. At the end of recovery, 
  *the TM will invoke the endRecovery method, to indicate to the
  *resource that whatever private logs it has, any remaining 
  *and non-recovered participants should be aborted.
  */
  
public interface RecoverableResource
{
	
	/**
	 * Initialize this resource with the recovery service.
	 * This method is called by the transaction service during
	 * intialization of the transaction service or when the
	 * resource is added, whichever comes last. If the 
	 * resource wants to recover, it should subsequently 
	 * ask the recoveryService
	 * to do so.
	 * @param recoveryService The recovery service. This instance
	 * can be used by the resource to ask recovery from the 
	 * transaction engine. 
	 * @throws ResourceException On errors.
	 */

	public void setRecoveryService ( RecoveryService recoveryService )
	throws ResourceException;
	
    /**
     *Recover the partially reconstructed Participant.
     *@param participant A partially recovered Participant.
     *@exception ResourceException On failure.
     *@return boolean True iff reconstruction was successful.
     *If the resource is not responsible for the given participant,
     *then this will return false.
     *A Participant can use this to iterate over all resources in order
     *to eventually recover itself. This is particularly
     *useful if the Participant instance can not serialize 
     *its full state, because some of it is on its backside 
     *resource (as, for instance, in XA).
     *This way, the TransactionalResource can be used to 
     *assist in reconstruction of the Participant's state.
     */
     
    public boolean recover ( Participant participant ) 
        throws ResourceException;
    
    /**
     *Notify the resource that recovery is ended. 
     *Called by TM at end of recovery; any remaining
     *resourcetransactions (i.e., that have not been 
     *associated with any recover call) should be rolled back.
     *This is because if the were not recovered by the TM, 
     *then surely they are not supposed to be indoubt
     *(the TM recovers ALL indoubt work!) and should be
     *rolled back.
     *
     *@exception ResourceException On failure.
     */
     
    public void endRecovery () throws ResourceException;
    
    /**
     *Close the resource for shutdown.
     *This notifies the resource that it is no longer needed.
     */

    public void close() throws ResourceException;
    
    /**
     *Get the name of the resource. Names should be unique 
     *within one TM domain.
     *@return String The name.
     */
     
    public String getName();
    
    /**
     *Test if a resource is the same as another one.
     */

    public boolean isSameRM(RecoverableResource res) 
        throws ResourceException;

    /**
     * Test if the resource is closed.
     * @return boolean True if the resource is closed.
     */
    public boolean isClosed();
    
        

}
