package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * An exception indicating that a message type is not understood.
 */

public class IllegalMessageTypeException extends Exception
{
    public IllegalMessageTypeException ()
    {
        super ();
    }

    public IllegalMessageTypeException ( String s )
    {
        super ( s );
    }

}
