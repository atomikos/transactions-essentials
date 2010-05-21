/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.diagnostics;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 *
 *A simple console to a PrintStream.
 */

public class PrintStreamConsole implements Console
{
    protected PrintStream printstream_ = null;
    
    private int level_ = Console.WARN;
    
    private DateFormat formatter_;
    
    /**
     *Constructor.
     *     
     *
     *@param printstream The PrintStream to print to.
     */

    public PrintStreamConsole(PrintStream printstream) 
    {
        printstream_ = printstream;
        formatter_ = new SimpleDateFormat ( "yy-MM-dd hh:mm:ss,SSS" );
    }
    
   	private synchronized void printLineSuffix() throws IOException
   	{
   		 //printstream_.print ( now.toString() );
   		 StringBuffer buf = new StringBuffer();
   		 
   		 //include thread name
   		 Thread current = Thread.currentThread();
   		 String tName = current.getName();
   		 buf.append ( formatter_.format( new Date() ) );
   		 buf.append ( " [" + tName + "]" );
		 printstream_.print ( buf.toString() );
   	}
    
    
    /**
     *@see com.atomikos.diagnostics.Console
     */

    public synchronized void println(String string) throws IOException
    {
        printLineSuffix();
        print ( string );
        printstream_.println();
   
    }
    
       
    /**
     *@see com.atomikos.diagnostics.Console
     */

    public synchronized void print (String string) throws IOException
    {
        
        printstream_.print (string);
   
    }
    
    
    /**
     *@see com.atomikos.diagnostics.Console
     */
     
     public void close() throws IOException
     {
        printstream_.close();	
     }
     
     public void finalize()  throws Throwable
     {  
        try {
             close();
             
        }
        catch ( Exception e ) {}
        finally {
          super.finalize();	
        }	
     }

    
    public void println(String string, int level) throws IOException
    {
        if ( getLevel() >= level ) {
        	
        	println ( string );
        } 
    	   
    }

   
    public void print(String string, int level) throws IOException
    {
        
        if ( getLevel() >= level ) print ( string );
        
    }

   
    public void setLevel(int level)
    {
        level_ = level;
        
    }
    
    public int getLevel()
    {
    	return level_;
    }
}
