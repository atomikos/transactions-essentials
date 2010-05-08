//$Id: TestResultParticipant.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Id: TestResultParticipant.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: TestResultParticipant.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:07  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:55  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:18  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/08/05 15:03:29  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.3.2.1  2004/06/14 08:09:10  guy
//Merged redesign2002 with redesign2003.
//
//Revision 1.2.8.1  2002/05/06 13:19:43  guy
//Modified import/export design to allow # protocols to co-exist, and introduced
//a portable propagation mechanism.
//Revision 1.3  2004/03/22 15:36:53  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.2.10.1  2003/06/20 16:31:33  guy
//*** empty log message ***
//
//Revision 1.2  2002/03/01 10:47:39  guy
//Updated to new prepare exception of HeurMixed.
//
//Revision 1.1  2002/02/18 13:32:20  guy
//Added test files to package under CVS.
//
//Revision 1.3  2001/03/07 18:57:36  pardon
//Continued on CoordinatorImp.
//
//Revision 1.2  2001/03/06 19:05:39  pardon
//Adapted heuristic message passing and completed Participant implementation.
//
//Revision 1.1  2001/03/05 19:14:39  pardon
//Continued working on 2pc messaging.
//

package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.SysException;

public class TestResultParticipant implements Participant , java.io.Serializable
{
    protected int code_=-1;
    //what to simulate
    
    protected String tid_ = null;
    
    protected boolean forgetCalled_ = false;
    
    protected HeuristicMessage[] msgs_ = new HeuristicMessage[0];
    //messages, should not be null to make logwrites work!
    
    protected void setCode( int code )
    {
        code_ = code;
    }
    
    public TestResultParticipant()
    {
    }

    public TestResultParticipant(int code, HeuristicMessage[] msgs)
    {
        this(code);
        msgs_ = msgs;
    }

    /**
     *Constructor.
     *
     *@param code Determines testing behaviour.
     */

    public TestResultParticipant(int code)
    {
        msgs_ = new HeuristicMessage[1];
        msgs_[0] = new StringHeuristicMessage ( "test " );
        code_ = code;
        
    }
    
    public TestResultParticipant ( int code, String tid )
    {
        code_ = code;
        tid_ = tid;	
    }
    
    public boolean recover()
    {
        return true; 
    }
    
    
    public int getCode()
    {
        return code_;
    }
    
    public Object getState()
    {
      return null;	
    }
    
    public boolean voteOK()
    {
        return ( code_ == 1 || code_ > 4 );
    }
    
    public boolean voteReadOnly()
    {
        return code_ == 1;
    }
    
    public boolean heurMixed( boolean preparePhase )
    {
        //return (code_ == 2 || ( code_ == 6 && !preparePhase ));
        return  ( ( code_ == 6 )&& !preparePhase );
    }	
    
    public boolean heurCommit( boolean preparePhase)
    {
        //return (code_ == 4 || ( code_ == 5 && !preparePhase ));
        return ( (code_ == 5 )&& !preparePhase );
    }
    
    public boolean heurAbort()
    {
        return code_ == 8;
    }
    
    public boolean heurHazard( boolean preparePhase) 
    {
        return (  ( preparePhase && code_ == 3 ) || ( code_ == 7 && !preparePhase) );
    }

    public void setCascadeList(java.util.Dictionary allParticipants)
        throws SysException{}

    

    public int getGlobalSiblingCount(){return 0;}


    public void setGlobalSiblingCount(int count){}
    
    public String getURI()
    {
        return null; 
    }

    /**
     *If code 0, the instance will answer no for prepare.
     *If 1, prepare will answer readonly. If 2, prepare will simulate a 
     *HEUR_MIXED. If 3, prepare will simulate a HEUR_HAZARD.
     *If 4, a HEUR_COMMIT is returned.
     *All other cases are voted OK.
     */

     public int prepare()
        throws RollbackException,
	     HeurHazardException,
	     HeurMixedException,
	     SysException
    {  
        switch (code_) {
        case 0:
	  throw new RollbackException("NO VOTE");
	
        case 1:
	  return Participant.READ_ONLY;
       
        case 2: 
                throw new HeurHazardException ( msgs_ );
        case 4:
	  throw new HeurHazardException(msgs_);
        case 3:
	  throw new HeurHazardException(msgs_);

     //   case 4:
//	  throw new HeurCommitException(msgs_);

        default:
	  return Participant.READ_ONLY+1;
        }
    }


    /**
     *Code 8 triggers heuristic rollback, 2 heuristic mixed, 3 heuristic 
     *hazard, 7 hazard, 6 mixed. Other codes are OK.
     */

    public HeuristicMessage[] commit(boolean onePhase)
        throws HeurRollbackException,
	     HeurMixedException,
	     HeurHazardException,
	     SysException
    {

        
        switch (code_) {
  
        //case 1:
//            throw 
//                new SysException("commit after readonly");
                
        case 8:
	  throw 
	      new HeurRollbackException(msgs_);
	
       // case 3:
//	  throw new HeurHazardException(msgs_);

       // case 2:
//	  throw new HeurMixedException(msgs_);
//          
        case 7:
	  throw new HeurHazardException(msgs_);

        case 6:
	  throw new HeurMixedException(msgs_);

        default:
	  return msgs_;
        }
    }

    /**
     *Code 5 is heuristic commit, 6 is mixed and 7 hazard.
     *All others are ok.
     */

    public HeuristicMessage[] rollback()
        throws HeurCommitException,
	     HeurHazardException,
	     HeurMixedException,
	     SysException
    {  
       

        switch (code_) {
        
       // case 1:
//            throw 
//                new SysException("rollback after readonly");
                
        //case 2:
//            throw new HeurMixedException(msgs_);
       // case 3:
//            throw new HeurHazardException(msgs_);
        //case 4: 
//            throw new HeurCommitException(msgs_);
        case 5:
                //System.err.println ( "Participant with code 5; throwing heur commit " );
	  throw 
	     new HeurCommitException(msgs_);
	
        case 7:
	  throw new HeurHazardException(msgs_);

        case 6:
	  throw new HeurMixedException(msgs_);
        default:
	  return msgs_;
        }
    }

    public HeuristicMessage[] getHeuristicMessages()
    {
      return null;	
    }
    
    public boolean forgotten()
    {
        return forgetCalled_; 
    }

    public void forget() {
        forgetCalled_ = true;
    }
    
    public boolean equals ( Object o ) 
    {
        if ( o == null ||  ! ( o instanceof TestResultParticipant ) ) 
            return false;	  
        
        TestResultParticipant other = ( TestResultParticipant ) o;
        if ( tid_ == null || other.tid_ == null )
            return super.equals ( o );
            
        return tid_.equals ( other.tid_ );
    }
    
    public int hashCode() 
    {
        if ( tid_ == null ) 
            return 1;
        
        return tid_.toString().hashCode();	
    }
}


