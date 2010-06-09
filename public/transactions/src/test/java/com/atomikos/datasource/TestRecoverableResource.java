package com.atomikos.datasource;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryService;

 /**
  *
  *
  *A test instance of a recoverable resource. This class can be
  *used as a reference case or merely for testing.
  *
  */

public class TestRecoverableResource
implements RecoverableResource
{
     private String name_;
     //the unique name
     
     private boolean recoveryEnded_;
     //true ASA endRecovery() called.
     
     private boolean closed_;
     //true ASA close() called.
     
     private boolean failOnClose_;
     //to simulate issue 10038
     
      /**
       *Creates a new instance with a given name.
       *
       *@param name The given name.
       */
       
     public TestRecoverableResource ( String name )
     {
          name_ = name;
          recoveryEnded_ = false;
          closed_ = false;
          failOnClose_ = false;
     }
     
     /**
      * Sets failure mode on close.
      *
      */
     public void setFailOnClose()
     {
    	 	failOnClose_ = true;
     }
     
      /**
       *Test method to check if endRecovery() was called.
       *@return boolean True iff endRecovery() was called.
       *Note: right after calling close() this will return false in any case.
       */
       
     public boolean isRecoveryEnded()
     {
          return recoveryEnded_;
     }
     
      /**
       *Test method to check if close was called.
       *@return boolean True iff close was called.
       */
       
     public boolean isClosed()
     {
          return closed_;
     }
     
      /**
       *@see RecoverableResource
       */
       
     public void endRecovery()
     throws ResourceException
     {
          recoveryEnded_ = true;
     }
     
      /**
       *@see RecoverableResource
       *
       */
       
     public boolean isSameRM ( RecoverableResource res )
     throws ResourceException
     {
          return res == this;
     }
     
      /**
       *@see RecoverableResource
       */
       
     public String getName()
     {
          return name_;
     }
     
      /**
       *@see RecoverableResource
       */
       
     public void close() throws ResourceException
     {
          recoveryEnded_ = false;
          closed_ = true;
          if ( failOnClose_ ) 
        	  	throw new ResourceException ( "Simulated error" );
     }
      
      /**
       *This method by default ALWAYS returns true, no matter what
       *the participant is.
       *
       *@see RecoverableResource.
       */
       
     public boolean recover ( Participant p )
     throws ResourceException
     {
          return true;
     }

    /**
     * @see com.atomikos.datasource.RecoverableResource#startRecovery(com.atomikos.icatch.RecoveryService)
     */
    public void setRecoveryService(RecoveryService recoveryService) throws ResourceException
    {
        if ( recoveryService != null ) recoveryService.recover();
        
    }
}
