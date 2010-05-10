//$Id: TestSubTxAwareParticipant.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Log: TestSubTxAwareParticipant.java,v $
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
//Revision 1.1.1.1  2006/03/09 14:59:17  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2004/10/12 13:03:52  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2002/02/18 14:45:28  guy
//Added test files.
//
//Revision 1.1  2002/02/18 13:32:27  guy
//Added test files to package under CVS.
//

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
 
