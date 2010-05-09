//$Id: SubTxAwareAdaptor.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: SubTxAwareAdaptor.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:17  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:45  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:38  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:01  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:32  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/08/05 15:04:23  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.2  2004/10/12 13:03:53  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//


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
