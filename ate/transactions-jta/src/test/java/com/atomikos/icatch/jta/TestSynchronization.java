//$Id: TestSynchronization.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//$Log: TestSynchronization.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:13  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:56  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:19  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2004/10/12 13:03:46  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.3  2004/09/20 14:50:36  guy
//Added tests for new recovery and JBoss changes.
//
//Revision 1.2  2002/02/25 14:51:55  guy
//Updated test infrastructure.
//
//Revision 1.1  2002/02/18 13:32:24  guy
//Added test files to package under CVS.
//

package com.atomikos.icatch.jta;
import javax.transaction.Status;
import javax.transaction.Synchronization;

/**
 *
 *
 *A test helper to test synchronization.
 */
 
public class TestSynchronization implements Synchronization
 {
      protected boolean beforecalled_;
      protected boolean aftercalled_;
      protected int completionStatus_;
      
      
      public TestSynchronization()
      {
          beforecalled_ = false;
          aftercalled_ = false;
          completionStatus_ = Status.STATUS_UNKNOWN;
      }
      
      public void beforeCompletion()
      {
          beforecalled_ = true;	
      }	
      
      public void afterCompletion( int status ) 
      {
      	  if ( aftercalled_ ) 
      	  	throw new RuntimeException ( "AfterCompletion called twice!");
      	  completionStatus_ = status;
          aftercalled_ = true;	
          
      }
      
       public boolean isCalledBefore()
       {
          return beforecalled_;	
       }
       
       public boolean isCalledAfter()
       {
          return aftercalled_;	
       }
       
       public int getCompletionStatus()
       {
       		return completionStatus_;
       }
 }
