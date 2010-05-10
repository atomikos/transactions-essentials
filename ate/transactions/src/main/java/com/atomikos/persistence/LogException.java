package com.atomikos.persistence;

/**
 * 
 */

public class LogException extends Exception
{
    protected java.util.Stack errors_ = null;

    public LogException ()
    {
        super ();
    }

    public LogException ( String s )
    {
        super ( s );
    }

    public LogException ( String s , java.util.Stack errors )
    {
        super ( s );
        errors_ = (java.util.Stack) errors.clone ();
    }

    public LogException ( java.util.Stack errors )
    {
        super ();
        errors_ = (java.util.Stack) errors.clone ();
    }

    public java.util.Stack getErrors ()
    {
        return errors_;
    }

}
