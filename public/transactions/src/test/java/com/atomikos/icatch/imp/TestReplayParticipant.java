package com.atomikos.icatch.imp;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.SysException;

 /**
  *
  *
  *A test participant for testing replay of 2PC.
  */
  
  public class TestReplayParticipant
  extends TestResultParticipant
  implements Runnable
  {
      private boolean replayExpected_;
      private boolean replayDone_;
      private boolean committed_;
      private transient RecoveryCoordinator rc_;
      
      public TestReplayParticipant ( HeuristicMessage[] msgs )
      {
          super ( 100 , msgs );
          replayExpected_ = false;
          replayDone_ = false;
          committed_ = false;
      } 
      
      public void startReplay()
      {
          replayExpected_ = true;
          Thread t = new Thread ( this );
          t.setDaemon ( true );
          t.start();     
      }
      
      private boolean isCommitted()
      {
          return committed_; 
      }
      
       /**
        *Wait for a limited time ( 10 sec ) for replay.
        *
        *@return boolean True iff replay done.
        */
        
      public synchronized  boolean waitUntilReplayDone ()
      throws InterruptedException
      {
          System.err.println ( "TestReplayParticipant: about to wait 10 secs for replay" );
          wait ( 10000 );
          return replayDone_; 
      }
      
      public void setRecoveryCoordinator ( RecoveryCoordinator rc )
      {
          rc_ = rc; 
      }
      
      public synchronized HeuristicMessage[] commit ( boolean onePhase )
        throws HeurRollbackException,
             HeurMixedException,
             HeurHazardException,
             SysException
      {
          if ( onePhase ) {
              System.err.println ( "Error: onePhase commit for TestRepalyParticipant" ); 
          }
          if ( ! replayExpected_ ) {
              System.err.println ( "Commit in TestReplayParticipant - throwing hazard" );
              committed_ = true;
              throw new HeurHazardException ( getHeuristicMessages() );
          }
          else {
              System.err.println ( "TestReplayParticipant: replay of commit" );
              replayDone_ = true;
              notifyAll();
          }
          return super.commit ( onePhase ); 
      }
      
      public synchronized HeuristicMessage[] rollback()
        throws HeurCommitException,
             HeurHazardException,
             HeurMixedException,
             SysException
      {
          if ( ! replayExpected_ ) {
              committed_ = false;
              throw new HeurHazardException ( getHeuristicMessages() );
          }
          else {
              System.err.println ( "TestReplayParticipant: replay of rollback" );
              replayDone_ = true;
              notifyAll();
          }
          return super.rollback(); 
      }
      
      public void run()
      {
          try {
              synchronized ( this ) {
                  wait ( 100 );                
              }
              Boolean res = null;
              res = rc_.replayCompletion ( this );
              if ( res == null )
                  throw new Exception ( "Commit anomaly: rec. coord. does not know?" );
              if ( res.booleanValue() != committed_ )
                  throw new Exception ( "Commit anomaly: rec. coord. has diff. decision?" );
              
          }
          catch ( Exception e ) {
                e.printStackTrace();
          }    
      }    
      
      public boolean equals ( Object o ) 
      {
          return o == this;
      }
      
      public int hashCode()
      {
          return  100;
      }
  }
