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
