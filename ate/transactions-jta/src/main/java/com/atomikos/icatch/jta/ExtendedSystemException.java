package com.atomikos.icatch.jta;

import java.util.Enumeration;
import java.util.Stack;

import javax.transaction.SystemException;

/**
 * 
 * 
 * A better system exception, containing nested errors in a stack.
 */

public class ExtendedSystemException extends SystemException
{
    private Stack errors_;

    public ExtendedSystemException ( String msg , Stack errors )
    {
        super ( msg );
        errors_ = errors;
    }

    /**
     * Get any nested errors as a stack.
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
