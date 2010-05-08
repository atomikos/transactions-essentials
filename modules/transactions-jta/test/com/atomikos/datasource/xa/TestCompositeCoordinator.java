//$Id: TestCompositeCoordinator.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//$Log: TestCompositeCoordinator.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:13  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:55  guy
//Import.
//
//Revision 1.4  2006/03/21 16:14:18  guy
//Changed to new interface.
//
//Revision 1.3  2006/03/21 14:11:20  guy
//Replaced UnavailableException with UnsupportedOperationException.
//
//Revision 1.2  2006/03/21 13:23:24  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:17  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2004/10/12 13:04:52  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2004/03/22 15:39:34  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.10.1  2003/06/20 16:32:08  guy
//*** empty log message ***
//
//Revision 1.1  2002/02/18 13:32:09  guy
//Added test files to package under CVS.
//

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
