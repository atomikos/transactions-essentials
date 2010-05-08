//$Id: ExtentParticipant.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Log: ExtentParticipant.java,v $
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
//Revision 1.5  2005/08/05 15:04:21  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.4  2004/10/12 13:03:51  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.3  2004/03/22 15:38:06  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.2.10.1  2003/06/20 16:31:47  guy
//*** empty log message ***
//
//Revision 1.2  2002/03/01 10:48:03  guy
//Updated to new prepare exception of HeurMixed.
//Added more failure modes and test facilities.
//
//Revision 1.1  2002/02/22 09:03:11  guy
//Adapted for ReleaseTester necessities.
//

package com.atomikos.icatch;

 /**
  *
  *
  *A test participant implementation that can be used for 
  *testing the extent/orphan detection functionality.
  */
  
public class ExtentParticipant
extends AbstractParticipant
{
      private int count_;
      //the no of invocations
      
      private int detected_;
      //the no of detected invocations by the root
      
       /**
        *Create a new instance.
        */
        
      public ExtentParticipant ()
      {
          this ( new StringHeuristicMessage ( "ExtentParticipant" ) );
      } 
      
       /**
        *Create a new instance with a given heuristic message.
        *
        *@param msg The message for heuristics.
        */
        
      public ExtentParticipant ( HeuristicMessage msg )
      {
          super ( false , msg  );
          count_ = 0;
          detected_ = 0;
      }
      
       /**
        *Get the local count of invocations.
        *@return int The local count of invocations,
        *as set previously.
        */
        
      public int getLocalSiblingCount()
      {
          return count_; 
      }
      
       /**
        *Set the local sibling count for simulating a number
        *of invocations  to this instance.
        *
        *@param count The local sibling count.
        *Setting this value simulates so many invocations
        *and can be used to test orphan detection.
        */
        
      public void setLocalSiblingCount ( int count )
      {
          count_ = count;   
      }
      
       /**
        *@see Participant
        */
        
      public void setGlobalSiblingCount ( int count )
      {
          detected_ = count; 
      }
      
    /**
     *@see Participant
     */
    
    public int prepare()
        throws RollbackException,
	     HeurHazardException,
	     HeurMixedException,
	     SysException
    {
        if ( count_ != detected_ )
            throw new RollbackException();
        else 
            return super.prepare();
    }
      
}
