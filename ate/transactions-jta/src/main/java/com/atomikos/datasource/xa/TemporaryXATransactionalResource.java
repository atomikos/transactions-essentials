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

package com.atomikos.datasource.xa;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.ResourceException;
import com.atomikos.diagnostics.Console;

/**
 * 
 * 
 * 
 * 
 * 
 * A class for temporary resources. This is useful for configurations that don't
 * register any XATransactionalResources in advance (zero setup). These cases
 * require a dynamic and temporary XATransactionalResource to be added. This
 * resource will be closed with the connection it is created on, and therefore
 * is temporary.
 */

public class TemporaryXATransactionalResource extends XATransactionalResource
{
	/**
	 * Max length in bytes of the resource name
	 */
	private static final int MAX_BYTES = 45;
	
	/**
	 * Truncates name to fit in 45 bytes
	 * @param name
	 * @return
	 */
	private static final String truncateNameTo45Bytes ( String name )
	{
		String ret = name;
		if ( ret.getBytes().length > MAX_BYTES ) {
			ret = name.substring ( 0 , MAX_BYTES );
		}
		//still too long? -> must be double-byte encoding
		//so halve bytes
		if ( ret.getBytes().length > MAX_BYTES ) {
			ret = name.substring ( 0 , MAX_BYTES / 2 );
		}
		return ret;
	}

    public TemporaryXATransactionalResource ( XAResource xares )
    {
        super ( truncateNameTo45Bytes ( xares.toString () ) );
        xares_ = xares;
        // get xa resource a first time, to
        // so that needsRefresh() means that the
        // connection is closed (as opposed to not inited)
        getXAResource ();

        try {
            if ( !xares.isSameRM ( xares ) )
                printMsg ( "XAResource " + xares + " of class "
                        + xares.getClass ().getName ()
                        + " does not correctly implement isSameRM(): "
                        + "use explicit resource registration to save memory.",
                        Console.WARN );
        } catch ( XAException e ) {
            // ignore: this is vanilla code anyway
        }
    }

    /**
     * @see com.atomikos.datasource.xa.XATransactionalResource#refreshXAConnection()
     */
    protected XAResource refreshXAConnection () throws ResourceException
    {
        // return the same instance at all times, since there is no connection
        // factory
        // for temporary resources
        return xares_;
    }

    public boolean isClosed ()
    {
        return needsRefresh ();
    }

}
