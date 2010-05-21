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
