/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 *
 *
 * An abstract superclass for all XidFactory implementations. This class
 * provides the functionality to create really unique XIDs.
 *
 *
 *
 *
 */
abstract class AbstractXidFactory implements XidFactory
{

   private static final int MAX_LENGTH_OF_COUNTER = String.valueOf(Long.MAX_VALUE).length();
 
   static AtomicLong counter = new AtomicLong(0);

    public AbstractXidFactory ()
    {
        super ();

    }

    /**
     * @see com.atomikos.datasource.xa.XidFactory
     */

    public XID createXid ( String tid , String branchIdentifier, String uniqueResourceName )
    {

    	if ( branchIdentifier.getBytes().length + MAX_LENGTH_OF_COUNTER > XID.MAXBQUALSIZE ) {
    		// see case 73086
    		throw new IllegalArgumentException ( "Value too long: " + branchIdentifier );
    	}

        // first increment counter to make sure it is
        // different from the last call that was done
        // by the SAME tid (works because calls within
        // one TID are serial)
        return new XID (tid, branchIdentifier + counter.incrementAndGet(), uniqueResourceName);
    }

}
