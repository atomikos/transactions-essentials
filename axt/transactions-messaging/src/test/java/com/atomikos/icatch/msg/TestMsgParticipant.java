package com.atomikos.icatch.msg;
import com.atomikos.icatch.system.Configuration;

 /**
  *Copyright &copy; 2002, Guy Pardon. All rights reserved.
  *
  *A test utility for testing a message transport and message 
  *protocol. Instances of this class can be used to simulate
  *endpoints of remote 2PC participants.
  */
  
public class TestMsgParticipant
implements MessageListener
{
  
     /**
      *Constant indicating a heuristic abort is to 
      *be simulated on commit.
      */
      
    public static final int HEURISTIC_ABORT = 0;
    
    /**
      *Constant indicating a heuristic commit is to 
      *be simulated on rollback.
      */
      
    public static final int HEURISTIC_COMMIT = 1;
    
    /**
      *Constant indicating a heuristic mixed is to 
      *be simulated on commit/rollback.
      */
    
    public static final int HEURISTIC_MIXED = 2;
    
    /**
      *Constant indicating a heuristic hazard is to 
      *be simulated on commit/rollback.
      */
    
    public static final int HEURISTIC_HAZARD = 3;
    
    /**
      *Constant indicating a readonly is to 
      *be simulated at prepare.
      */
      
    public static final int READ_ONLY = 4;
    
    /**
      *Constant indicating that rollback is to be 
      *simulated at 1pc or prepare.
      */
    
    public static final int ROLLED_BACK = 5;
    
     /**
      *Constant indicating normal 2PC 
      *behaviour: prepare OK and commit/abort as well.
      */
      
    public static final int NORMAL = 6;
    
    //
    //BELOW ARE INTERNAL STATE CODES
    //
    
     /**
      *The initial state.
      */
      
    private static final int INITIAL_STATE =0;
    
    /**
      *The prepared state.
      */
    
    private static final int PREPARED_STATE = 1;
    
     /**
      *The end state.
      */
      
    private static final int END_STATE = 2;
    
     /**
      *A heuristic state.
      */
      
    private static final int HEURISTIC_STATE = 3;
    
    //
    //INSTANCE VARIABLES
    //
  
    private Transport transport_; 
    //the transport implementation to use
    
    private int state_;
    //the internal state
    
    private String address_;
    //our address
    
    private String uri_;
    //the uri for this instance
    
    private String coordinatorAddress_;
    //the coordinator's address
    
    private String coordinatorUri_;
    //the coordinator's uri
    
    private int behaviour_;
    //code that indicates which behaviour.
    
    private boolean heuristicReturnsError_;
    //if false, then a commit/rb in heuristic state
    //will return a StateMessage
    
    private boolean prepareReceived_;
    //true ASA prepare comes in
    
    private boolean commitReceived_;
    //true ASA commit comes in
    
    private boolean rollbackReceived_;
    //true ASA rollback comes in
    
    private boolean forgetReceived_;
    //true ASA forget comes in
    
    public TestMsgParticipant ( 
        Transport transport , String uri , String address, 
        String coordinatorAddress, String coordinatorUri )
    {
          transport_ = transport;
          uri_ = uri;
          address_ = address;
          coordinatorAddress_ = coordinatorAddress;
          coordinatorUri_ = coordinatorUri;
          reset();
    }
  
   /**
    *Resets the internal state.
    *
    */
    
    public void reset()
    {
          prepareReceived_ = false;
          commitReceived_ = false;
          rollbackReceived_ = false;
          forgetReceived_ = false;
          heuristicReturnsError_ = true;
          setState ( INITIAL_STATE );
          behaviour_ = NORMAL;
    }
    
     /**
      *Set the behaviour of this  participant.
      *@param behaviour One of the predefined codes.
      */
      
    public void setBehaviour ( int behaviour )
    {
          behaviour_ = behaviour;
    }
    
     /**
      *Set the reaction to incoming commit/rollback msgs
      *in a heuristic state. By default, this will return an 
      *error message. If unset, then a committed/rolledback
      *StateMessage will be returned.
      *@param val The new value.
      */
      
    public void setHeuristicReturnsErrorMessage ( boolean val )
    {
        heuristicReturnsError_ = val; 
    }
    
     /**
      *Process an incoming message.
      *@param msg The message.
      *@return TransactionMessage The return,
      *or null if none.
      */
      
    private synchronized TransactionMessage processMessage ( 
        TransactionMessage msg ) 
    {
        TransactionMessage reply = null;
        Transport fact = transport_;
        
        if ( msg instanceof PrepareMessage ) {
            prepareReceived_ = true;
            if ( state_ != INITIAL_STATE ) {
                throw new IllegalStateException (
                          "Unexpected PrepareMessage in state " +
                          state_ );
            }
            switch ( behaviour_ ) {
                  case ROLLED_BACK:
                      reply = fact.createErrorMessage ( 
                          ErrorMessage.ROLLBACK_ERROR , uri_ , 
                          msg.getSenderURI() , msg.getSenderAddress() );
                          setState ( END_STATE );
                      break;
                  case READ_ONLY:
                      reply = fact.createPreparedMessage ( true ,
                          uri_ , 
                          msg.getSenderURI() , msg.getSenderAddress() );
                          setState ( END_STATE );
                      break;
                      
                  default:
                      //whether heuristic or normal, we do have 
                      //a succesful prepare
                      reply = fact.createPreparedMessage ( false ,
                          uri_ , 
                          msg.getSenderURI() , msg.getSenderAddress() );
                      setState ( PREPARED_STATE );
                      break;
            }
        }
        else if ( msg instanceof CommitMessage ) {
        	   
            commitReceived_ = true;
            if ( ! ( state_  == INITIAL_STATE || 
                     state_ == PREPARED_STATE ||
                     state_ == HEURISTIC_STATE ) )
                     throw new IllegalStateException ( 
                     "Unexpected CommitMessage in state " +
                          state_ );
            switch ( behaviour_ ) {
              
                  case HEURISTIC_ABORT:
                      if ( heuristicReturnsError_ ) {
                          reply = fact.createErrorMessage ( 
                              ErrorMessage.HEUR_ROLLBACK_ERROR,
                              uri_, msg.getSenderURI() , msg.getSenderAddress() );
                      }
                      else {
                          reply = fact.createStateMessage ( 
                              new Boolean ( false ) , uri_, 
                              msg.getSenderURI() , msg.getSenderAddress() );
                      }
                      setState ( HEURISTIC_STATE ); 
                      break;
                      
                  case HEURISTIC_MIXED:
                      reply = fact.createErrorMessage ( 
                              ErrorMessage.HEUR_MIXED_ERROR,
                              uri_, msg.getSenderURI() , msg.getSenderAddress() );
                      setState ( HEURISTIC_STATE ); 
                      break;
                      
                  case HEURISTIC_HAZARD:
                      reply = fact.createErrorMessage ( 
                              ErrorMessage.HEUR_HAZARD_ERROR,
                              uri_, msg.getSenderURI() , msg.getSenderAddress() );
                      setState ( HEURISTIC_STATE ); 
                      break;
                  
                  default:
                      //other cases return StateMessage and reach end 
                      reply = fact.createStateMessage ( 
                              new Boolean ( true ) , uri_, 
                              msg.getSenderURI() , msg.getSenderAddress() );
                      setState ( END_STATE );
                      break;
                  
            }
        }
        else if ( msg instanceof RollbackMessage ) {
              rollbackReceived_ = true;
              if ( ! ( state_  == INITIAL_STATE || 
                     state_ == PREPARED_STATE ) )
                     throw new IllegalStateException ( 
                     "Unexpected RollbackMessage in state " +
                          state_ );
            switch ( behaviour_ ) {
              
                  case HEURISTIC_COMMIT:
                      if ( heuristicReturnsError_ ) {
                          reply = fact.createErrorMessage ( 
                              ErrorMessage.HEUR_COMMIT_ERROR,
                              uri_, msg.getSenderURI() , msg.getSenderAddress() );
                      }
                      else {
                          reply = fact.createStateMessage ( 
                              new Boolean ( true ) , uri_, 
                              msg.getSenderURI() , msg.getSenderAddress() );
                      }
                      setState ( HEURISTIC_STATE ); 
                      break;
                      
                  case HEURISTIC_MIXED:
                      reply = fact.createErrorMessage ( 
                              ErrorMessage.HEUR_MIXED_ERROR,
                              uri_, msg.getSenderURI() , msg.getSenderAddress() );
                      setState ( HEURISTIC_STATE ); 
                      break;
                      
                  case HEURISTIC_HAZARD:
                      reply = fact.createErrorMessage ( 
                              ErrorMessage.HEUR_HAZARD_ERROR,
                              uri_, msg.getSenderURI() , msg.getSenderAddress() );
                      setState (  HEURISTIC_STATE ); 
                      break;
                  
                  default:
                      //other cases return StateMessage and reach end 
                      reply = fact.createStateMessage ( 
                              new Boolean ( false ) , uri_, 
                              msg.getSenderURI() , msg.getSenderAddress() );
                      setState ( END_STATE );
                      break;
                  
            }
        }
        else if ( msg instanceof ForgetMessage ) {
              forgetReceived_ = true;
              if ( state_ != HEURISTIC_STATE ) 
                  throw new IllegalStateException ( 
                     "Unexpected ForgetMessage in state " +
                     state_ );
              setState (  END_STATE );
        }
        else throw new IllegalStateException ( 
              "Unexpected incoming msg: " + 
              msg.getClass().getName() );
        
        return reply;
    }
    
    private synchronized void setState ( int state ) 
    {
        state_ = state;
        
    }
    
     /**
     *Initializes the test participant.
     *Should be called before the test starts; i.e., before any
     *message could be sent that are intended for this
     *participant. This method registers this instance
     *to listen for incoming messages (on the transport)
     *that are intended for this target (based on the
     *getTargetURI() value of an incoming message).
     */
     
    public void init()
    throws Exception
    {
        transport_.registerMessageListener ( this ); 
    }
    
     /**
      *Tests if prepare was received.
      *@return boolean The result of the test.
      */
      
    public boolean hasReceivedPrepare()
    {
        return prepareReceived_; 
    }
    
    /**
      *Tests if commit was received.
      *@return boolean The result of the test.
      */
      
    public boolean hasReceivedCommit()
    {   
        return commitReceived_;
    }
    
    /**
      *Tests if rollback was received.
      *@return boolean The result of the test.
      */
      
    public boolean hasReceivedRollback()
    {
        return rollbackReceived_; 
    }
    
    /**
      *Tests if forget was received.
      *@return boolean The result of the test.
      */
      
    public boolean hasReceivedForget()
    {
        return forgetReceived_; 
    }
    
    
    /**
     *@see MessageListener 
     */
     
     public boolean messageReceived ( TransactionMessage msg , Transport t )
     {  
    	    
        TransactionMessage reply = null;
        try {
            if ( msg.getTargetURI().equals ( uri_ ) ) {
                //the message is for us.
            	    Configuration.logDebug ( "TestMessageParticipant: received msg: " + msg );
                reply = processMessage ( msg );
                
                if ( reply != null ) {
                	   Configuration.logDebug ( "TestMessageParticipant: replying with msg " + reply );
                    transport_.send ( reply );
                }
                
            }
            
        }
        catch ( Exception e ) {
            System.err.println ( "Error in TestMsgParticipant.messageReceived" );
            e.printStackTrace(); 
        }
        return state_ != END_STATE;
     }
    
    
}

