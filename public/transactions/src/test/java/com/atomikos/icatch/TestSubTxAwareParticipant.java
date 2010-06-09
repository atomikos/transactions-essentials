package com.atomikos.icatch;

/**
 *
 *
 *A test helper to check if notification works.
 */
 
public  class TestSubTxAwareParticipant implements SubTxAwareParticipant
 {
      private boolean notified_ ;
      
      public TestSubTxAwareParticipant () {
          notified_ = false;	
      }
      
      public void committed ( CompositeTransaction ct )
      {
          notified_ = true;	
      }
      
      public void rolledback ( CompositeTransaction ct ) 
      {
          notified_ = true;	
      }
      
      public boolean isNotified() 
      {
          return notified_;	
      }
 	
 }
 
