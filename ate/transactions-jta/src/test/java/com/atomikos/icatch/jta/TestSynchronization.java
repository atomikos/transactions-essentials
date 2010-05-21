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
