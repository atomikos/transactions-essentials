//$Id: TestRecoverableResource.java,v 1.2 2006/09/15 08:39:26 guy Exp $
//$Log: TestRecoverableResource.java,v $
//Revision 1.2  2006/09/15 08:39:26  guy
//Merged-in changes from 3.0.1 release.
//
//Revision 1.1.1.1.2.1  2006/09/14 07:34:52  guy
//Added test for issue 10038
//
//Revision 1.1.1.1  2006/08/29 10:01:06  guy
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
//Revision 1.1.1.1  2006/03/09 14:59:16  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2004/10/12 13:04:50  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.3  2004/10/01 15:16:29  guy
//Adapted to new recovery..
//
//Revision 1.2  2004/09/06 09:28:07  guy
//Added setRecoveryService method, to enable recovery after restart.
//
//Revision 1.1  2002/02/20 17:31:43  guy
//Added a test resource.
//

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
