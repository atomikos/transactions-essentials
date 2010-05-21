package com.atomikos.persistence.imp;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import com.atomikos.persistence.LogException;
import com.atomikos.util.TestCaseWithTemporaryOutputFolder;

public class FileLogStreamTestJUnit extends TestCaseWithTemporaryOutputFolder 
{
	private FileLogStream logstream;
	private Stack stack;
	
	public FileLogStreamTestJUnit ( String name )
	{
		super ( name );
	}
	
	private FileLogStream getLogStream() throws IOException
	{
		String dir = getTemporaryOutputDir();
		if ( ! dir.endsWith ( "/" ) ) dir = dir + "/";
		return new FileLogStream ( dir  , "TESTLOG", null );
	
	}
	
	protected void setUp()
	{
		super.setUp();
		stack = new Stack();
		
		try {
			 logstream = getLogStream();
			 logstream.recover();
		} catch (Exception e) {
			failTest ( e.getMessage() );
		} 
	}
	
	protected void tearDown()
	{
		try {
			logstream.close();
		} catch (LogException e) {
			failTest ( e.getMessage() );
		}
		super.tearDown();
	}
	
	public void testCheckpoint()
	throws Exception
	{
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
               failTest ( "checkpoint does not work: " +
                   " found " + nxt.toString() + " instead of Second" ); 
               
        }
	}
	
	public void testCrashDuringCheckpoint()
	throws Exception
	{
        //next, simulate a crash where both checkpoint and 
        //old file are there.
        
		//first write a checkpoint to resort to after crash
		testCheckpoint();
		
        logstream.setCrashMode();
        
        stack.push ( "Third" );
        try {
           logstream.writeCheckpoint ( stack.elements() );
           failTest ( "Checkpoint in crash mode: " +
               "should yield exception" );
        }
        catch ( LogException le ) {}
        logstream.close();
        
        //again, on recovery, only a String "Second" should be returned
        //this is because the checkpoint ( new ) file should be 
        //regarded as incomplete.
        
        logstream = getLogStream();
        Vector recovered = logstream.recover();
        Enumeration enumm = recovered.elements();
        while ( enumm.hasMoreElements() ) {
           String nxt = ( String ) enumm.nextElement();
           if ( ! nxt.equals ( "Second" ) )
               failTest ( "checkpoint does not work: " +
                   " found " + nxt.toString() + " instead of Second" ); 
        }
	}
}
