package com.atomikos.icatch.trmi;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.SubTxAwareParticipant;

 /**
  *Copyright &copy; 2001, Atomikos. All rights reserved.
  *
  *An adaptor for registering a subtx aware participant from
  *a remote client. This instance is responsible for converting
  *the CompositeTransaction argument of both subtx aware
  *methods to a proxy, since this proxy typically depends on
  *the comm. mechanism used.
  *
  *Note: this class is NOT the same as the SubTxAwareWrapper:
  *this class is used ONLY by the trmi transaction manager/
  */
  
class SubTxAwareAdaptor implements SubTxAwareParticipant
{
      private TrmiTransactionManager tm_;
      private SubTxAwareParticipant wrapped_;
      
      SubTxAwareAdaptor ( TrmiTransactionManager tm, SubTxAwareParticipant wrapped )
      {
          tm_ = tm; 
          wrapped_ = wrapped;
      }
      
       /**
       *@see com.atomikos.icatch.SubTxAwareParticipant
       */
       
      
      public void committed ( CompositeTransaction tx )
      {
          //called by LOCAL tm on commit of tx,
          //we need to convert tx to a proxy instance to pass on
          //to the remote subtx aware
          
          CompositeTransaction proxy = tm_.createProxy ( tx );
          wrapped_.committed ( proxy );
      }
      
        /**
       *@see com.atomikos.icatch.SubTxAwareParticipant
       */
       
      public void rolledback ( CompositeTransaction tx )
      {
          //called by LOCAL tm on rollback of tx.
          //we need to convert tx to a PROXY instance to passs
          //on to remote subtx aware
          
          CompositeTransaction proxy = tm_.createProxy ( tx );
          wrapped_.rolledback ( proxy );
      }         
}
