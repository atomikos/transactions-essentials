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
