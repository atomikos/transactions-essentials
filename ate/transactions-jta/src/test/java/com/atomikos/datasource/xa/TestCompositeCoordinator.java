package com.atomikos.datasource.xa;
import java.util.Hashtable;

import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.SysException;

/**
 *
 *
 *A test stub for composite coordinator, for testing xa classes.
 */
 
 class TestCompositeCoordinator implements CompositeCoordinator
 {
   protected String root_ ;
   
   protected Hashtable participants_;
   
   TestCompositeCoordinator ( String root )
   {
      root_ = root;	
      participants_ = new Hashtable();
   }
   
   public String getCoordinatorId()
   {
      return root_;	
   }
    
    /**
     *Add a new participant to the coordinator.
     *
     *@param participant The participant to add.
     *@return RecoveryCoordinator Whom to ask for indoubt timeout resolution.
     *@exception SysException Unexpected.
     *@exception IllegalStateException Illegal state.
     *@exception RollbackException If tx was marked for rollback.
     */

    public RecoveryCoordinator addParticipant ( Participant participant )
        throws SysException,
             java.lang.IllegalStateException,
             RollbackException
    {
    	participants_.put ( participant , new Object() );
    	return null;
    }


    public RecoveryCoordinator getRecoveryCoordinator()
    {
        return null;	
    }
    
    public com.atomikos.icatch.HeuristicMessage[] getTags(){
        return null;
    }

    /**
     *Add a synch. callback.
     *
     *@param sync The callback object.
     *@exception RollbackException If rollback set.
     *@exception IllegalStateException If no tx longer active.
     *@exception SysException Unexptected failure.
     */

    public void registerSynchronization(Synchronization sync)
        throws RollbackException,
             IllegalStateException,
             UnsupportedOperationException,
             SysException
      {
          throw new RuntimeException ( "Not implemented");	
      } 
  

      Hashtable getParticipants() 
      {
          return participants_;	
      }

    public Boolean isRecoverableWhileActive ()
    {
        
        return new Boolean ( false );
    }

    public void setRecoverableWhileActive () throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
        
    }
	
 }
