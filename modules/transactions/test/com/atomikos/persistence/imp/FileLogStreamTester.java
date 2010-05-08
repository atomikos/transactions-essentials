//$Id: FileLogStreamTester.java,v 1.2 2006/09/19 08:03:53 guy Exp $
//$Log: FileLogStreamTester.java,v $
//Revision 1.2  2006/09/19 08:03:53  guy
//FIXED 10050
//
//Revision 1.1.1.1  2006/08/29 10:01:07  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:57  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:21  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.1  2002/02/18 14:45:33  guy
//Added test files.
//

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
             FileLogStream logstream = new FileLogStream ( "./" , filename_ , null );
             
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
             
             logstream = new FileLogStream ( "./" , filename_ , null );
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
