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
