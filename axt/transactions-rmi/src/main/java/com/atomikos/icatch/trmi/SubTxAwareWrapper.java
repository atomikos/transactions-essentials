package com.atomikos.icatch.trmi;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.SubTxAwareParticipant;

 /**
  *Copyright &copy; 2001, Atomikos. All rights reserved.
  *
  *A wrapper for adding an exported SubTxAwareParticipant instance on a client
  *VM that is different from the server VM.
  *
  *In general, this will be the preferred class to wrap an implementation
  *of RemoteSubTxAware with before adding to the composite transaction.
  *NOTE: the wrapped instance should be EXPORTED before 
  *wrapping it and adding it to the transaction!
  *Otherwise, the callbacks will NOT work as expected.
  */
  
  public class SubTxAwareWrapper 
  implements SubTxAwareParticipant
  {
      private RemoteSubTxAware remote_;
      
      public SubTxAwareWrapper ( RemoteSubTxAware remote )
      {
          remote_ = remote; 
      }
      
      /**
       *@see com.atomikos.icatch.SubTxAwareParticipant
       */
       
      
      public void committed ( CompositeTransaction tx )
      {
          try {
              remote_.committed ( tx );
          }
          catch ( Exception e ) {
                  e.printStackTrace();
                //NO runtime exception here:
                //we are just notifying as a courtesy, no 
                //guarantees are offered.
          } 
      }
      
      /**
       *@see com.atomikos.icatch.SubTxAwareParticipant
       */
       
      public void rolledback ( CompositeTransaction tx )
      {
          try {
              remote_.rolledback ( tx );
          }
          catch ( Exception e ) {
                //NO runtime exception here:
                //we are just notifying as a courtesy, no 
                //guarantees are offered.
          } 
      }
  }
