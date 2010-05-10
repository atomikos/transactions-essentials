//$Id: PrintStreamConsole.java,v 1.1.1.1 2006/08/29 10:01:15 guy Exp $
//$Log: PrintStreamConsole.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:15  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:51  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:40  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:37  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:03  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:43  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.7  2005/08/09 15:25:46  guy
//Updated javadoc.
//
//Revision 1.6  2005/05/10 08:45:32  guy
//Merged-in changes of Transactions_2_03 branch.
//
//Revision 1.5.2.1  2005/03/30 15:26:43  guy
//Rebuilt.
//
//Revision 1.5  2004/10/12 13:05:04  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.4  2004/09/01 13:41:37  guy
//Merged in changes of TransactionsRMI 1.22
//
//Revision 1.3  2004/03/22 15:39:47  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.2.10.1  2003/06/20 16:32:16  guy
//*** empty log message ***
//$Id: PrintStreamConsole.java,v 1.1.1.1 2006/08/29 10:01:15 guy Exp $
//$Log: PrintStreamConsole.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:15  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:51  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:40  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:37  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:03  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:43  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.7  2005/08/09 15:25:46  guy
//Updated javadoc.
//
//Revision 1.6  2005/05/10 08:45:32  guy
//Merged-in changes of Transactions_2_03 branch.
//
//Revision 1.5.2.1  2005/03/30 15:26:43  guy
//Rebuilt.
//
//Revision 1.5  2004/10/12 13:05:04  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.4  2004/09/01 13:41:37  guy
//Merged in changes of TransactionsRMI 1.22
//
//Revision 1.2.12.1  2004/04/30 14:34:01  guy
//Added the concept of different levels.
//
//Revision 1.2  2002/01/29 11:26:28  guy
//Updated to latest state: repository seemed to be outdated?
//
//Revision 1.1  2001/03/01 19:27:40  pardon
//For diagnosing servers.
//


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
