package com.atomikos.icatch.jaxws;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * An exception to signal propagation errors during import. This exception is
 * used to indicate that the transaction headers of an incoming SOAP message are
 * incompatible with the local propagation preference.
 * 
 * 
 */
public class PropagationException extends Exception
{

    /**
     * 
     */
    public PropagationException ()
    {
        super ();
    }

    /**
     * @param reason
     */
    public PropagationException ( String reason )
    {
        super ( reason );
    }

    /**
     * @param reason
     * @param cause
     */
    public PropagationException ( String reason , Throwable cause )
    {
        super ( reason , cause );
    }

    /**
     * @param cause
     */
    public PropagationException ( Throwable cause )
    {
        super ( cause );
    }

}
