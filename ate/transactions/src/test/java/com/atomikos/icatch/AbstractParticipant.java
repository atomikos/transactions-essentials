//$Id: AbstractParticipant.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Log: AbstractParticipant.java,v $
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
//Revision 1.8  2005/08/05 15:04:21  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.7  2004/10/12 13:03:51  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//$Id: AbstractParticipant.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//Revision 1.6  2004/03/22 15:38:06  guy
//$Id: AbstractParticipant.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: AbstractParticipant.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//
//$Id: AbstractParticipant.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//Revision 1.5.2.1  2003/06/20 16:31:47  guy
//$Id: AbstractParticipant.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//*** empty log message ***
//$Id: AbstractParticipant.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//
//$Id: AbstractParticipant.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//Revision 1.5  2003/03/11 06:39:13  guy
//$Id: AbstractParticipant.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: AbstractParticipant.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//
//Revision 1.4.4.1  2002/11/18 17:50:50  guy
//Corrected tests.
//
//Revision 1.4  2002/03/01 10:48:03  guy
//Updated to new prepare exception of HeurMixed.
//Added more failure modes and test facilities.
//
//Revision 1.3  2002/02/22 16:55:22  guy
//Updated tests.
//
//Revision 1.2  2002/02/22 09:03:10  guy
//Adapted for ReleaseTester necessities.
//
//Revision 1.1  2002/02/12 21:19:44  guy
//Added generic test files.
//

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
