//$Id: CompositeCoordinatorProxy.java,v 1.1.1.1 2006/10/02 15:21:16 guy Exp $
//$Log: CompositeCoordinatorProxy.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:16  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:45  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:38  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:01  guy
//Import.
//
//Revision 1.4  2006/03/21 16:14:41  guy
//Added setter for active recovery.
//
//Revision 1.3  2006/03/21 14:12:07  guy
//Replaced UnavailableException with UnsupportedOperationException.
//
//Revision 1.2  2006/03/21 13:24:01  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:32  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.7  2005/08/09 15:24:13  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.6  2005/08/05 15:04:23  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.5  2004/10/12 13:03:53  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//$Id: CompositeCoordinatorProxy.java,v 1.1.1.1 2006/10/02 15:21:16 guy Exp $
//Revision 1.4  2004/03/22 15:38:14  guy
//$Id: CompositeCoordinatorProxy.java,v 1.1.1.1 2006/10/02 15:21:16 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: CompositeCoordinatorProxy.java,v 1.1.1.1 2006/10/02 15:21:16 guy Exp $
//
//$Id: CompositeCoordinatorProxy.java,v 1.1.1.1 2006/10/02 15:21:16 guy Exp $
//Revision 1.3.2.1  2003/06/20 16:31:52  guy
//$Id: CompositeCoordinatorProxy.java,v 1.1.1.1 2006/10/02 15:21:16 guy Exp $
//*** empty log message ***
//$Id: CompositeCoordinatorProxy.java,v 1.1.1.1 2006/10/02 15:21:16 guy Exp $
//
//$Id: CompositeCoordinatorProxy.java,v 1.1.1.1 2006/10/02 15:21:16 guy Exp $
//Revision 1.3  2003/03/11 06:39:16  guy
//$Id: CompositeCoordinatorProxy.java,v 1.1.1.1 2006/10/02 15:21:16 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: CompositeCoordinatorProxy.java,v 1.1.1.1 2006/10/02 15:21:16 guy Exp $
//
//Revision 1.2.4.1  2003/01/29 17:20:07  guy
//Adapted to use JNDI binding instead of Naming of RMI.
//
//Revision 1.2  2001/10/29 16:38:11  guy
//Changed UniqueId for String.
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//
//Revision 1.1  2001/03/23 17:02:01  pardon
//Added some files to repository.
//

package com.atomikos.icatch.trmi;
import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.SysException;

/**
 *Copyright &copy; 2001, Atomikos. All rights reserved.
 *
 *A proxy for a coordinator. To allow remote subtxs to access ancestor
 *functionality.
 */

class CompositeCoordinatorProxy implements CompositeCoordinator , 
					java.io.Serializable
{
    protected String root_ =  null;
    
    protected RecoveryCoordinatorProxy recoverycoordinator_ = null;

    protected boolean recoverableWhileActive_;
    
    /**
     *Constructor.
     *
     *@param coordinator The instance for which to act as proxy.
     *@param recoveryservername The jndi name of a recovery server.
     *@param providerUrl The URL of the JNDI provider.
     *@param initialContextFactory The name of the initial context factory.
     */

    public CompositeCoordinatorProxy ( CompositeCoordinator coordinator,
			         String recoveryservername , 
			         String initialContextFactory , 
			         String providerUrl )
    {
        root_ = coordinator.getCoordinatorId();
        recoverableWhileActive_ = coordinator.isRecoverableWhileActive().booleanValue();
        recoverycoordinator_ = 
	new RecoveryCoordinatorProxy ( 
              root_ , recoveryservername , 
              initialContextFactory , providerUrl );
    }
    
        

    /**
     *@see CompositeCoordinator
     */
     
    public String getCoordinatorId()
    {
        return root_;
    }

    /** 
     *@see CompositeCoordinator
     */
     
    public HeuristicMessage[] getTags()
    {
        return null;
    }

    /**  
     *@see CompositeCoordinator.
     */

    public RecoveryCoordinator getRecoveryCoordinator()
    {
        return recoverycoordinator_;
    }
      
    

    /**
     *@see CompositeCoordinator.
     */

    public void registerSynchronization(Synchronization sync)
        throws RollbackException,
	     IllegalStateException,
	     UnsupportedOperationException,
	     SysException
    {
        throw new UnsupportedOperationException ("registerSynchronization on proxy");
    }



    public Boolean isRecoverableWhileActive ()
    {
        return new Boolean ( recoverableWhileActive_);
    }
 

    public void setRecoverableWhileActive()
    {
        throw new UnsupportedOperationException();
    }
  
    public String toString()
    {
    	return getCoordinatorId();
    }
 

}
