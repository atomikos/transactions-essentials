package com.atomikos.icatch;

 /**
  *
  *
  *This class is an abstract participant whose subclasses serve
  *as simulations of problem cases.
  */

public abstract class AbstractParticipant implements Participant
{
    private boolean readOnly_;
    //if true: prepare votes readonly
    
    private HeuristicMessage[] msg_;
    //the heuristic message to use
    
    
    private boolean terminated_;
    //true ASA 2PC termination done
    
     /**
      *Create a new instance with or without readonly behaviour.
      *@param readOnly If true, then the prepare phase will
      *vote readonly.
      *@param msg The heuristic message.
      */
      
    AbstractParticipant ( boolean readOnly , HeuristicMessage msg )
    {
        readOnly_ = readOnly;
        msg_ = new HeuristicMessage[1];
        msg_[0] = msg;
        terminated_ = false;
    }
    
     /**
      *Test method to check if 2PC termination was done.
      *
      *@return boolean True iff commit/rollback/forget was called.
      */
      
    public boolean isTerminated()
    {
        return terminated_; 
    }
    
    /**
     *@see Participant
     */
    
    public boolean recover() 
    throws SysException
    {
          //by default: does nothing
          return true;
    }
    
     /**
      *@see Participant
      */
      
    public String getURI()
    {
         return this.toString();
    }
    
     /**
     *@see Participant
     */

    public void setCascadeList(java.util.Dictionary allParticipants)
        throws SysException
    {
        //nothing by default      
    }

     /**
     *@see Participant
     */
     
    public void setGlobalSiblingCount(int count)
    {
        //default does nothing 
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
        if ( readOnly_ ) {
            //System.err.println ( "Prepare for read-only participant." );
            return Participant.READ_ONLY;
        }
        else {
            //System.err.println ( "Prepare for non-read-only participant." );

            return Participant.READ_ONLY + 1;
        }
    }
      
     /**
     *@see Participant
     */

    public HeuristicMessage[] commit ( boolean onePhase )
        throws HeurRollbackException,
	     HeurHazardException,
	     HeurMixedException,
	     RollbackException,
	     SysException
    { 
        
//        if ( readOnly_ )
//            System.err.println ( "Commit for read-only participant" );
//        else
//          System.err.println ( "Commit for participant." );
        terminated_ = true;
        return msg_;
    }
    
     /**
     *@see Participant
     */
     
    public HeuristicMessage[] rollback()
        throws HeurCommitException,
	     HeurMixedException,
	     HeurHazardException,
	     SysException
    {
        terminated_ = true;
        return msg_; 
    }
    
    /**
     *@see Participant
     */
     
    public void forget()
    {
        terminated_ = true;
    }
     
    /**
     *@see Participant
     */
     
    public HeuristicMessage[] getHeuristicMessages()
    {
        return msg_; 
    }
    
    
    public boolean equals ( Object o ) 
    {
        if ( ! ( o instanceof AbstractParticipant ) )
            return false;
        
        return o == this; 
    }
    
    
}
