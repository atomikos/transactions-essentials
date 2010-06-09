package com.atomikos.icatch;
import java.util.Properties;
import java.util.Stack;

import com.atomikos.icatch.imp.AbstractCompositeTransaction;

 /**
  *A stub class for simulating imported composite transactions.
  */

public class CompositeTransactionStub 
extends AbstractCompositeTransaction
implements
CompositeCoordinator, RecoveryCoordinator
{
    private String tid_;
    

    
     /**
      *Constructs a new instance around a given Xid.
      *@param xid The Xid to wrap.
      */
      
    public CompositeTransactionStub ( String tid )
    {
        tid_ = tid;
        properties_ = new Properties();
    } 
    
    public String getTid()
    {
        return tid_;
    }
    
    public CompositeCoordinator getCompositeCoordinator()
    {
        return this; 
    }
    
    public Stack getLineage()
    {
        Stack ret = new Stack();
        ret.push ( this );
        return ret;
    }
    
    public boolean isAncestorOf ( CompositeTransaction ct )
    {
        //return false by default; this means that heavy lock inheritance
        //will not work but it is easier to implement.
        return false; 
    }
    
    public boolean isDescendantOf ( CompositeTransaction ct )
    {
        //return false by default; this means that heavy lock inheritance
        //will not work but it is easier to implement.
        return false; 
    }
    
    public boolean isLocal()
    {
        //the transaction is simulating an imported composite transaction,
        //so return false.
        return false; 
    }
    
    public boolean isRelatedTransaction ( CompositeTransaction ct )
    {
        return isSameTransaction ( ct );
    }
    
    public boolean isSameTransaction ( CompositeTransaction ct )
    {
        return ct.getTid().equals ( getTid() ); 
    }
    
    public boolean isRoot()
    {
        //stub transactions are treated as a root
        //for which a local subtransaction will be created
        return true; 
    }
    
    public boolean isSerial()
    {
        //J2EE does not support non-serial transactions
        return true; 
    }
    
    public String getCoordinatorId()
    {
        //we are simulating a root, so the root ID
        //is our own TID
        return getTid(); 
    }
    
    public HeuristicMessage[] getTags()
    {
        return null;
    }
    
    public RecoveryCoordinator getRecoveryCoordinator()
    {
        return this; 
    }
    
    
    public Boolean replayCompletion ( Participant participant )
    {
        //act as if we don't know anything
        //because XA does not support this 
        return null; 
    }
    
    //
    //BELOW ARE ALL METHODS THAT ARE NOT NEEDED/SUPPORTED
    //FOR XA IMPORTS
    //
    
    
    public Object getState()
    {
        throw new UnsupportedOperationException ( "Not implemented" ); 
    }
    
    public void registerSynchronization ( Synchronization s )
    {
        throw new UnsupportedOperationException ( "Not implemented" ); 
    }
    
    public RecoveryCoordinator addParticipant ( Participant p )
    {
        throw new UnsupportedOperationException ( "Not implemented" ); 
    }
    
    public void addSubTxAwareParticipant ( SubTxAwareParticipant s )
    {
        throw new UnsupportedOperationException ( "Not implemented" ); 
    }
    
    public TransactionControl getTransactionControl()
    {
        throw new UnsupportedOperationException ( "Not implemented" ); 
    }

    public String getURI()
    {
        
        return "CompositeTransactionStub";
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
