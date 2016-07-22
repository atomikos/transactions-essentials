/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.ResourceException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * A class for temporary resources. This is useful for configurations that don't
 * register any XATransactionalResources in advance (zero setup). These cases
 * require a dynamic and temporary XATransactionalResource to be added. This
 * resource will be closed with the connection it is created on, and therefore
 * is temporary.
 */

public class TemporaryXATransactionalResource extends XATransactionalResource
{
	private static final Logger LOGGER = LoggerFactory.createLogger(TemporaryXATransactionalResource.class);

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
        super ( truncateNameTo45Bytes ( String.valueOf(xares.hashCode()) ) );
        xares_ = xares;
        // get xa resource a first time, to
        // so that needsRefresh() means that the
        // connection is closed (as opposed to not inited)
        getXAResource ();

        try {
            if ( !xares.isSameRM ( xares ) )
            	LOGGER.logWarning("XAResource " + xares + " of class "
                        + xares.getClass ().getName ()
                        + " does not correctly implement isSameRM(): "
                        + "use explicit resource registration to save memory.");
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
        // factory for temporary resources
        return xares_;
    }

    public boolean isClosed ()
    {
        return needsRefresh ();
    }

}
