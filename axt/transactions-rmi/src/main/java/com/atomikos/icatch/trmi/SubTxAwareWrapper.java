//$Id: SubTxAwareWrapper.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: SubTxAwareWrapper.java,v $
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
//Revision 1.1.1.1  2006/03/09 14:59:33  guy
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
