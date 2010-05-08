//
//  TestSynchronization.java
//  icatch
//
//  Created by guy on Tue Mar 27 2001.
//  
//

package com.atomikos.icatch;
import com.atomikos.icatch.system.Configuration;

/**
*A test synchronization instance for testing notification mechanism.
*/

public class TestSynchronization implements Synchronization
{
    public static final int NOT_NOTIFIED = 0 , BEFORE_COMPLETION = 1,
                            COMMIT = 2 ,  ROLLBACK = 3;
                            
    private int status_ = NOT_NOTIFIED;
    
    private CompositeTransaction ct_ = null;
    
    public TestSynchronization ( )
    {
        status_ = NOT_NOTIFIED;
    }
    
    public TestSynchronization ( CompositeTransaction ct )
    {
        this();
        ct_ = ct;
    }
    
     /** 
      *@see Synchronization
      */
      
    public void beforeCompletion ()
    {
        status_ = BEFORE_COMPLETION;
        if ( ct_ != null ) {
            if ( !Configuration.
                  getCompositeTransactionManager().
                  getCompositeTransaction().equals ( ct_ ) ) {
                      throw new RuntimeException ( "TestSynchronization: wrong " +
                      "tx context in beforeCompletion?!?" );
            }
        }
    }
    
     /**
      *@see Synchronization
      */
      
    public void afterCompletion ( Object status )
    {
        if ( status.equals ( TxState.COMMITTING ) )
            status_ = COMMIT;
        else if ( status.equals ( TxState.ABORTING ) )
            status_ = ROLLBACK;
    }
    
      /**
      *Test if the instance was called before completion.
      *
      *@return boolean True iff called before completion.
      */
      
    public boolean isCalledBefore()
    {
        return status_ != NOT_NOTIFIED; 
    }
    
     /**
      *Test if the instance was called after completion.
      *
      *@return boolean True iff called after completion.
      */
      
    public boolean isCalledAfter()
    {   
        return status_ == COMMIT || status_ == ROLLBACK;
    }
    
     /** 
      *Get the status of this instance.
      *@return int One of the status codes.
      */
      
    public int getStatus() 
    {
        return status_;
    }
}
