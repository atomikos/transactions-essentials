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
