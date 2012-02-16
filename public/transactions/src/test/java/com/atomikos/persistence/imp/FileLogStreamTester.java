package com.atomikos.persistence.imp;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import com.atomikos.persistence.LogException;

 /**
  *
  *
  *A tester class for filelogstream.
  */

  public class FileLogStreamTester
  {
        static String filename_ = "FileLogStreamTester";

        public static void test () throws Exception
        {
             FileLogStream logstream = new FileLogStream ( "./" , filename_  );

             logstream.recover();

             //try some checkpoints
             Stack stack = new Stack();
             stack.push ( "First" );
             logstream.writeCheckpoint ( stack.elements() );
             stack.pop();
             stack.push ( "Second" );
             logstream.writeCheckpoint ( stack.elements() );
             logstream.close();

             //on recovery, only a String "Second" should be returned
             Vector recovered = logstream.recover();
             Enumeration enumm = recovered.elements();
             while ( enumm.hasMoreElements() ) {
                String nxt = ( String ) enumm.nextElement();
                if ( ! nxt.equals ( "Second" ) )
                    throw new Exception ( "checkpoint does not work: " +
                        " found " + nxt.toString() + " instead of Second" );

             }


             //next, simulate a crash where both checkpoint and
             //old file are there.

             logstream.setCrashMode();
             stack.pop();
             stack.push ( "Third" );
             try {
                logstream.writeCheckpoint ( stack.elements() );
                throw new Exception ( "Checkpoint in crash mode: " +
                    "should yield exception" );
             }
             catch ( LogException le ) {}
             logstream.close();

             //again, on recovery, only a String "Second" should be returned
             //this is because the checkpoint ( new ) file should be
             //regarded as incomplete.

             logstream = new FileLogStream ( "./" , filename_  );
             recovered = logstream.recover();
             enumm = recovered.elements();
             while ( enumm.hasMoreElements() ) {
                String nxt = ( String ) enumm.nextElement();
                if ( ! nxt.equals ( "Second" ) )
                    throw new Exception ( "checkpoint does not work: " +
                        " found " + nxt.toString() + " instead of Second" );
             }

        }

        public static void main ( String[] args )
        {
            if ( args.length == 0 ) {
                System.out.println ( "Arg required: filename for test output" );
                System.exit ( 1 );
            }

            System.out.println ( "Starting: FileLogStreamTester" );
            try {
                filename_ = args[0];
                test();
            }
            catch ( Exception e) {
                System.out.println ( "Error in test " +
                      e.getMessage() + e.getClass().getName() );
                e.printStackTrace();
            }
            finally {
                  System.out.println ( "Done:     FileLogStreamTester" );
            }

        }
  }
