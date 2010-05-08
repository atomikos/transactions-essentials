//$Id: CompositeTransactionStub.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//$Log: CompositeTransactionStub.java,v $
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
//Revision 1.3  2006/03/21 14:11:20  guy
//Replaced UnavailableException with UnsupportedOperationException.
//
//Revision 1.2  2006/03/21 13:23:24  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:16  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/08/09 15:25:33  guy
//Updated javadoc.
//
//Revision 1.3  2004/10/12 13:04:52  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2004/03/22 15:39:34  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.2.1  2003/06/20 16:32:08  guy
//*** empty log message ***
//
//Revision 1.1  2003/03/12 14:55:27  guy
//Re-added this file, since it is needed by XATester.
//
//Revision 1.2  2002/02/22 17:28:32  guy
//Updated: no RollbackException in addParticipant and registerSynch.
//
//Revision 1.1  2002/02/18 13:32:09  guy
//Added test files to package under CVS.
//


package com.atomikos.datasource.xa;
import java.util.Stack;

import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionControl;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.imp.AbstractCompositeTransaction;

 /**
  *
  *
  *A test stub for a composite transaction, for testing xa resource integration.
  */
  
  class CompositeTransactionStub extends AbstractCompositeTransaction
  {
    
    protected String tid_;
    protected boolean serial_;
    protected CompositeCoordinator coordinator_;
    protected TxState state_ = TxState.ACTIVE;
    protected Stack participants_ = new Stack();
    
    
    public CompositeTransactionStub  ( String tid , boolean serial , 
                                                        CompositeCoordinator coordinator )
    {
        tid_ = tid;
        coordinator_ = coordinator;
        serial_ = serial;	
        participants_ = new Stack();
    }
    
    /**
     *Add a new participant to the coordinator.
     *
     *@param participant The participant to add.
     *@return RecoveryCoordinator Whom to ask for indoubt timeout resolution.
     *@exception SysException Unexpected.
     *@exception IllegalStateException Illegal state.
     */

    public RecoveryCoordinator addParticipant ( Participant participant )
        throws SysException,
             java.lang.IllegalStateException
    {
    	participants_.push ( participant );
    	return null;
    }
    
    public Object getState() 
    {
        return state_;	
    }
     
    public boolean isRoot()
    {
      return false;	
    }
    
    public void setTag ( com.atomikos.icatch.HeuristicMessage tag )
    {
      
    }
    
    public boolean isLocal ()
    {
        return true; 
    }

    /**
     *Get the ancestor info.
     *@return Stack A stack of ancestor identifiers, topmost is root.
     */

    public Stack getLineage()
    {
      return null;	
    }


    /**
     *Getter for tid.
     *
     *@return String The tid for the tx.
     */

    public String getTid()
    {
        return tid_;	
    } 
    
      /**
     *Test if  we are  ancestor of ct.
     *
     *@param ct The argument to test for ancestor.
     *
     *@return boolean True iff ancestor of ct.
     */

    public boolean isAncestorOf( CompositeTransaction ct )
    {
        return false;	
    }

    /**
     *Test if we are descendant of ct.
     *
     *@param ct The argument to test for descendant.
     *
     *@return boolean True iff descendant of ct.
     */

    public boolean isDescendantOf( CompositeTransaction ct )
    {
      return false;	
    }


    /**
     *Test if related ones.
     *@return True if related. That is: if same root.
     */

    public boolean isRelatedTransaction ( CompositeTransaction ct )
    { 
        return false;
    }

      /**
     *Test if same as another one.
     *@return True if the same.
     */

    public boolean isSameTransaction ( CompositeTransaction ct )
    {
        return ct.getTid().equals( tid_ );	
    }

    /**
     *Get the coordinator for this tx.
     *
     *@return CompositeCoordinator The composite coordinator instance.
     *@exception SysException On failure.
     */
    
    public CompositeCoordinator getCompositeCoordinator() throws SysException
    {
        return coordinator_;	
    }
    
  
       /**
     *Register a participant on behalf of a subtransaction, to be 
     *notified as soon as the locally finished state is reached.
     *
     *@param subtxaware The participant to be notified 
     *on local termination.
     *
     *@exception IllegalStateException If no longer active.
     */

    public void addSubTxAwareParticipant( SubTxAwareParticipant subtxaware )
        throws SysException,
               UnsupportedOperationException,
             java.lang.IllegalStateException
     {
        throw new RuntimeException ( "Not implemented" );       	
      }

    /**
     *Test if serial tx or not.
     *
     *@return boolean True iff all for root is serial. 
     */
     
    public boolean isSerial()
    {
        return serial_;	
    }
  
       /**
     *Get a control object for this tx. Null if none.
     *
     *@return TransactionControl a control object. Null if none.
     */
     
    public TransactionControl getTransactionControl()
    {
        return null;	
    }

    /**
     *@see CompositeTransaction
     */
     
     public void registerSynchronization(Synchronization sync)
        throws
             IllegalStateException,
             UnsupportedOperationException,
             SysException
      {
            //do nothing
       }
	
  }
