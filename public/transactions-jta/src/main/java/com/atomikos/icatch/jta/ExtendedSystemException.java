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

package com.atomikos.icatch.jta;

import java.util.Enumeration;
import java.util.Stack;

import javax.transaction.SystemException;

/**
 * A better system exception, containing nested errors in a stack.
 */

public class ExtendedSystemException extends SystemException
{

    private Stack errors_;


    public ExtendedSystemException ( String msg , Throwable cause )
    {
        super ( msg);
        errors_ = new Stack();
        errors_.add(cause);
    }
   
    public ExtendedSystemException ( String msg , Stack errors )
    {
        super ( msg );
        errors_ = errors;
    }

    /**
     * Get any nested errors as a stack.
     * @deprecated
     *
     * @return Stack The nested error stack, or null if none.
     */

    public Stack getErrors ()
    {
        return (Stack) errors_.clone ();
    }

    public void printStackTrace ()
    {
        super.printStackTrace ();
        if ( errors_ != null ) {
            Enumeration enumm = errors_.elements ();
            while ( enumm.hasMoreElements () ) {
                Exception e = (Exception) enumm.nextElement ();
                e.printStackTrace ();
            }
        }
    }
}
