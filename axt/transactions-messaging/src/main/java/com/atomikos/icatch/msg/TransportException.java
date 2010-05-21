package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * An exception indicating an error at the transport level.
 */

public class TransportException extends Exception
{
    public TransportException ()
    {
        super ();
    }

    public TransportException ( String msg )
    {
        super ( msg );
    }

    public TransportException ( Exception cause )
    {
        super ( cause );

    }

}
