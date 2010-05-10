//$Id: SysException.java,v 1.2 2006/10/30 10:37:08 guy Exp $
//$Log: SysException.java,v $
//Revision 1.2  2006/10/30 10:37:08  guy
//Merged in changes of 3.1.0 release
//
//Revision 1.1.1.1.4.2  2006/10/05 06:21:54  guy
//FIXED 10066
//
//Revision 1.1.1.1.4.1  2006/09/29 07:15:29  guy
//FIXED 10065
//
//Revision 1.1.1.1  2006/08/29 10:01:07  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
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
//Revision 1.1.1.1  2006/03/09 14:59:22  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.7  2005/08/09 15:23:39  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.6  2004/10/25 08:45:56  guy
//Updated TODOs
//
//Revision 1.5  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.4  2004/09/01 13:39:02  guy
//Merged changes from TransactionsRMI 1.22.
//Corrected bug in SysException.printStackTrace.
//Added log method to Configuration.
//
//Revision 1.3  2004/03/22 15:36:53  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.2.10.1  2004/02/18 21:48:54  guy
//Removed calls to printErrorStack on SysException (replaced)
//
//Revision 1.2  2002/03/11 11:36:15  guy
//Added printErrorStack utility method.
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//
//Revision 1.4  2001/02/21 19:51:23  pardon
//Redesign!
//
//Revision 1.3  2001/02/21 09:58:36  pardon
//Added only the new version's files.
//
//Revision 1.1  2001/02/20 10:48:12  pardon
//Added Participant, TransactionalInvocation and SysException.
//

package com.atomikos.icatch;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 *
 *An exception for system errors with nested information.
 */
 
 
public class SysException extends RuntimeException
{
  
     /**
      *Print all stack traces, including those for nested errors.
      *Utility function for debugging purposes.
      *This method prints all errors and their stack traces
      *to StdErr.
      *@param e The exception to analyze.
      */
      
    private static void printNestedErrorStack ( SysException e )
    {
    	
        //FOLLOWING LINE REMOVED TO AVOID INFINITE RECURSION
        //IF SOMEONE PRINTS THE STACK TRACE OF A SYSEXCEPTION
        //WITH printStackTrace()
        //e.printStackTrace();
        Stack errors = e.getErrors();
        while ( errors != null && ! errors.empty() ) {
            System.err.println ( "Nested exception is: " );
            Exception nxt = ( Exception ) errors.pop();
            if ( nxt instanceof SysException ) {
                SysException se = ( SysException ) nxt;
                nxt.printStackTrace();
                printNestedErrorStack ( se );
            }
            else {
                nxt.printStackTrace();
            }
        } 
    }
    
    private static void addStackTraceElementsToList ( StackTraceElement[] elements ,
    		List list )
    {
    		for ( int i = 0 ; i < elements.length ; i++ ) {
    			list.add ( elements[i] );

    		}
    }
    
    private java.util.Stack myErrors=null;
    
    public SysException (String msg)
    {
        super(msg);
    }
    public SysException (String msg,java.util.Stack nestedList)
    {
        super(msg);
        myErrors=(java.util.Stack) nestedList.clone();
    }
    
    private void addStackTraceToList ( List list ) 
    {
    		StackTraceElement[] elements = super.getStackTrace();
    		addStackTraceElementsToList ( elements , list );
    		Stack errors = getErrors();
    		while ( errors != null && ! errors.empty() ) {
    			Exception e = ( Exception ) errors.pop();
    			
    			if ( e instanceof SysException ) {
    				SysException se = ( SysException ) e;
    				se.addStackTraceToList ( list );
    			}
    			else {
    				elements = e.getStackTrace();
    				addStackTraceElementsToList ( elements , list );
    			}
    		}
    }
   
    public java.util.Stack getErrors() 
    {
        if (myErrors==null) 
	  return null;
        else 
	  return (java.util.Stack) myErrors.clone();
    }
    
    public void printStackTrace()
    {
    	
    	super.printStackTrace();
		printNestedErrorStack ( this );
    }
    
    public StackTraceElement[] getStackTrace()
    {
    		ArrayList elements = new ArrayList();
    		
    		this.addStackTraceToList ( elements );
    		
    		return ( StackTraceElement[] ) elements.toArray ( new StackTraceElement[0] );
    }
}
