package com.atomikos.icatch.msg;

 /**
  *Copyright &copy; 2002, Guy Pardon. 
  *
  *A test message coordinator. 
  *This class is in charge of simulating incoming 2PC events;
  *it also tests the reply messages as received from the TS.
  *The method <b>process</b> will run the message
  *based commit conversation agains the transport;
  *the transport will have the impression that a remote
  *actor is involved. By setting the behavioural code, 
  *different situations can be simulated.
  *
  */

public class TestMsgCoordinator
{
     /**
      *To indicate the coordinator should generate a rollback event
      *before the prepare phase.
      */
      
    public static final int ROLLBACK_BEFORE_PREPARE = 0;
    
     /**
      *To indicate that the coordinator should generate
      *a rollback event after the prepare.
      */
    
    public static final int ROLLBACK_AFTER_PREPARE = 1;
    
     /**
      *To indicate that the coordinator should generate
      *a commit one-phase event.
      */
    
    public static final int COMMIT_ONE_PHASE = 2;
    
     /**
      *To indicate that the coordinator should generate
      *a commit after prepare.
      */
    
    public static final int COMMIT_AFTER_PREPARE = 3;
    
    
     /**
      *Initial state: no events have been generated.
      */
      
    private static final int INITIAL_STATE = 0;
    
     /**
      *Intermediate state: a prepare has been generated,
      *but we are waiting for the reply.
      */
      
    private static final int PREPARE_SENT_STATE = 1;
    
    /**
      *End state: no more to do.
      */
      
    private static final int END_STATE = 2;
    
     /**
      *A prepared message has been received.
      */
      
    private static final int PREPARED_STATE = 3;
    
     /**
      *Indicates that aborts should be sent after prepare.
      */
      
    private static final int PREPARE_FAILED_STATE = 8;
    
      /**
       *We are aborting; a rollback has been sent
       *but no reply yet.
       */
       
    private static final int ABORTING_STATE = 4;
    
      /**
       *We are committing; a  commit has been sent
       *but no reply yet.
       */
       
    private static final int COMMITTING_STATE = 5;
    
     /**
      *A heuristic ERROR message has been received.
      */
    
    private static final int HEURISTIC_STATE = 6;
    
    
    private String uri_;
    //the URI of this coordinator
    
    private String address_;
    //the address of this coordinator
    
    private String targetAddress_;
    //the address of the target subordinate
    
    private String targetUri_;
    //the URI of the target subordinate

    
    private int state_;
    //the state we are in;
    //one of the predefined constants.
    //used to determine the next msg to simulate
    
    private Transport transport_;
    //the transport to use.
    
    private int sibcount_;
    //the global sibling count to set in the subordinate
    
    private CascadeInfo[] info_;
    //the info for cascading.
    
    private int behaviour_;
    //what behaviour
    
    private boolean preparedReceived_;
    //true ASA prepared comes in
    
    private boolean committedReceived_;
    //true ASA committed comes in
    
    private boolean rolledbackReceived_;
    //true ASA rolledback comes in
    
    private boolean replayReceived_;
    //true ASA replay comes in
    
    private int error_;
    //error code, or -1 if no error.
    
    
    TestMsgCoordinator ( Transport transport , 
        String uri , String address, 
        String targetAddress, String targetUri )
    {
          transport_ = transport;
          uri_ = uri;
          address_ = address;
          targetAddress_ = targetAddress;
          targetUri_ = CommitServer.createGlobalUri ( address , targetUri );
          reset();
          
    }
    
    public void reset()
    {
          state_ = INITIAL_STATE;
          behaviour_ = ROLLBACK_BEFORE_PREPARE;
          info_ = null;
          sibcount_ = 0;
          preparedReceived_ = false;
          committedReceived_ = false;
          rolledbackReceived_ = false;
          replayReceived_ = false;
          error_ = -1;
    }
    
     /**
      *Set the behaviour.
      *@param code One of the predefined behaviour codes.
      *Default is ROLLBACK_BEFORE_PREPARE
      */
      
    public void setBehaviour ( int code )
    {
        behaviour_ = code; 
    }
    
     /**
      *Set the orphan info to propagate in prepare.
      *@param globalSiblingCount The count.
      *@param info The cascade info.
      */
      
    public void setOrphanInfo ( int globalSiblingCount , 
        CascadeInfo[] info )
    {
        info_ = info;
        sibcount_ = globalSiblingCount;
    }
    
     /**
      *Utility method to test if orphan info should 
      *be included in prepare.
      *@return boolean True if orphan info must be added.
      */
      
    boolean includeOrphans()
    {
        return info_ != null; 
    }
    
     /**
      *Generate the next message and change state.
      *@TransactionMessage The message, as determined
      *by the state and the behavioural code set. Null if 
      *no next message.
      */
      
    private synchronized TransactionMessage getNextMessage()
    {
        TransactionMessage msg = null;
        Transport fact = transport_;
        switch ( state_ ) {
            
            case INITIAL_STATE: 
                if ( behaviour_ == ROLLBACK_BEFORE_PREPARE ) {
                    msg = fact.createRollbackMessage (
                            uri_ , targetUri_ , targetAddress_ );
                    state_ = ABORTING_STATE;
                }
                else if ( behaviour_ == COMMIT_ONE_PHASE ) {
                    msg = fact.createCommitMessage (
                          true , uri_ , targetUri_ , targetAddress_ );
                    state_ = COMMITTING_STATE;
                }
                else {
                    //send prepare
                    if ( includeOrphans() ) {
                        msg = fact.createPrepareMessage ( 
                              sibcount_ , info_ , uri_ , targetUri_ , targetAddress_ );
                    }
                    else {
                        msg = fact.createPrepareMessage (
                              uri_ , targetUri_ , targetAddress_ );
                    }
                    state_ = PREPARE_SENT_STATE;
                        
                }
                break;
                
                case PREPARE_FAILED_STATE:
                //this means that remote voted NO;
                //since there is only one remote, this concludes
                //the work. The remote will not expect any msgs
                
//                    msg = fact.createRollbackMessage (
//                            uri_ , targetUri_ , targetAddress_ );
                    state_ = END_STATE;
                    break;
                
                case PREPARED_STATE:
                    if ( behaviour_ == ROLLBACK_AFTER_PREPARE ) {
                          msg = fact.createRollbackMessage (
                            uri_ , targetUri_ , targetAddress_ );
                            state_ = ABORTING_STATE;
                    }
                    else if ( behaviour_ == COMMIT_AFTER_PREPARE ) {
                          msg = fact.createCommitMessage ( false , 
                              uri_ , targetUri_ , targetAddress_ );
                          state_ = COMMITTING_STATE;
                    }
                break;
                
                case END_STATE:
                    //do nothing, return null
                break;
                
                case HEURISTIC_STATE:
                    //generate forget msg
                    msg = fact.createForgetMessage ( 
                              uri_ , targetUri_ , targetAddress_ );
                    state_ = END_STATE;      
                        
                break;
            
            default: throw new IllegalStateException ( 
                        "Illegal state for getNextMessage(): " + state_ ); 
        }
        return msg;
    }
    
     /**
      *Tests if the state is the end state.
      *@return boolean True if end state reached.
      *In this case, no reply is awaited and no
      *more messages will be generated.
      */
      
    private boolean isDone()
    {
         return state_ == END_STATE; 
    }
    
     /**
      *Add a reply to this test coordinator.
      *@param msg The reply
      *@exception IllegalStateException If the state 
      *does not allow the given reply.
      */
    
    private synchronized void addReply ( TransactionMessage msg )
    {
    		if ( !msg.getTargetURI().equals ( uri_ ) ) return;
        switch ( state_ ) {
              
              case PREPARE_SENT_STATE:
                  if ( msg instanceof PreparedMessage ) {
                      preparedReceived_ = true;
                      PreparedMessage pmsg = ( PreparedMessage ) msg;
                      if ( pmsg.isReadOnly() ) {
                          state_ = END_STATE;
                      }
                      else 
                          state_ = PREPARED_STATE;
                  }
                  else if ( msg instanceof ErrorMessage ) {
                       ErrorMessage err = ( ErrorMessage ) msg;
                       error_ = err.getErrorCode();
                       if ( err.getErrorCode() == ErrorMessage.ROLLBACK_ERROR ) {
                            state_ = PREPARE_FAILED_STATE; 
                       }
                       else if (  ( err.getErrorCode() >= ErrorMessage.HEURISTIC_MIN ) &&
                                     ( err.getErrorCode() <= ErrorMessage.HEURISTIC_MAX ) )
                            state_ = HEURISTIC_STATE;
                  }
                  else throw new IllegalStateException ( 
                      "Illegal message during prepare" );
                  break;
                  
              case ABORTING_STATE:
                  if ( msg instanceof StateMessage ) {
                      StateMessage s = ( StateMessage ) msg;
                      if ( s.hasCommitted() == null || 
                            s.hasCommitted().booleanValue() )
                            throw new IllegalStateException ( 
                                "Commit received for abort msg?" );
                      state_ = END_STATE;
                      rolledbackReceived_ = true;
                  }
                  else if ( msg instanceof ErrorMessage ) {
                      ErrorMessage err = ( ErrorMessage ) msg;
                      state_ = HEURISTIC_STATE; 
                      error_ = err.getErrorCode();
                  }
                  else throw new IllegalStateException ( 
                        "Illegal reply to abort message" );
                      
                  break;
                  
                  case COMMITTING_STATE:
                  if ( msg instanceof StateMessage ) {
                      StateMessage s = ( StateMessage ) msg;
                      if ( s.hasCommitted() == null || 
                            !s.hasCommitted().booleanValue() )
                            throw new IllegalStateException ( 
                                "Rollback received for abort msg?" );
                      state_ = END_STATE;
                      committedReceived_ = true;
                  }
                  else if ( msg instanceof ErrorMessage ) {
                      ErrorMessage err = ( ErrorMessage ) msg;
                      state_ = HEURISTIC_STATE; 
                      error_ =err.getErrorCode();
                  }
                  else throw new IllegalStateException ( 
                        "Illegal reply to abort message" );
                      
                  break;
              
              default:
                  throw new IllegalStateException ( 
                  "Unexpected reply in state " + state_ );
        }
         
    }
    
     /**
      *Process this test coordinator's test case
      *on the given transport. When this method
      *returns, the test has finished.
      *
      */
      
    public void process ()
    throws Exception
    {
          int[] expected = 
            { TransactionMessage.PREPARED_MESSAGE,
              TransactionMessage.STATE_MESSAGE,
              TransactionMessage.ERROR_MESSAGE,
              TransactionMessage.REPLAY_MESSAGE
            };
            
          TransactionMessage msg = null;
          TransactionMessage reply = null;
          
          while ( ! isDone() ) {
              
              msg = getNextMessage();
              reply = null;
              
              //msg is null if no next one
              if ( msg != null ) {
               
                  if ( ! isDone() ) {
                      //a reply is awaited, since we are not done yet
                      reply = transport_.sendAndReceive ( 
                          msg , 70000 ,  expected );
                  }
                  else {
                      //we are done, so do not await a reply
                      transport_.send ( msg );
                  }
                  
              }
              
              if ( reply != null ) {
                  //if a reply was returned, add it
                  addReply ( reply );
              }
          }
    }
    
    /**
     *Tests if prepared was received.
     *@return boolean The result of the test.
     */
    
    public boolean hasReceivedPrepared()
    {
        return preparedReceived_; 
    }
    
    /**
     *Tests if committed was received.
     *@return boolean The result of the test.
     */
     
    public boolean hasReceivedCommitted()
    {
        return committedReceived_; 
    }
    
    /**
     *Tests if rolledback was received.
     *@return boolean The result of the test.
     */
     
    public boolean hasReceivedRolledback()
    { 
        return rolledbackReceived_;
    }
    
    /**
     *Tests if replay was received.
     *@return boolean The result of the test.
     */
     
    public boolean hasReceivedReplay()
    {
        return replayReceived_; 
    }
    
    /**
     *Tests if an error was received.
     *@return int The error code, or -1 if none.
     */
     
    public int getError()
    {
        return error_; 
    }
    
    
    
}
