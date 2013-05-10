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
 * An exception for unexpected system errors with nested information.
 */


public class SysException extends RuntimeException
{

	private static final long serialVersionUID = -9183281406145817016L;


	private static void printNestedErrorStack ( SysException e )
	{
		Stack<Exception> errors = e.getErrors();
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
			List<StackTraceElement> list )
	{
		for ( int i = 0 ; i < elements.length ; i++ ) {
			list.add ( elements[i] );

		}
	}

	private Stack<Exception> myErrors=null;

	public SysException (String msg)
	{
		super(msg);
		myErrors = new Stack<Exception>();
	}
	
	public SysException(String msg, Throwable cause) {
		super(msg,cause);
		myErrors = new Stack<Exception>();
	}
	
	/**
	 * @deprecated
	 */
	public SysException (String msg,Stack<Exception> nestedList)
	{
		super(msg);
		myErrors=(Stack<Exception>) nestedList.clone();
	}

	private void addStackTraceToList ( List<StackTraceElement> list )
	{
		StackTraceElement[] elements = super.getStackTrace();
		addStackTraceElementsToList ( elements , list );
		Stack<Exception> errors = getErrors();
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

	/**
	 * @deprecated
	 */
	public java.util.Stack<Exception> getErrors()
	{
		if (myErrors==null) return null;
		else return (Stack<Exception>) myErrors.clone();
	}

	public void printStackTrace()
	{
		
		super.printStackTrace();
		printNestedErrorStack ( this );
	}

	/**
	 * @deprecated
	 */
	public StackTraceElement[] getStackTrace()
	{
		List<StackTraceElement> elements = new ArrayList<StackTraceElement>();
		this.addStackTraceToList ( elements );
		return ( StackTraceElement[] ) elements.toArray ( new StackTraceElement[0] );
	}
}
