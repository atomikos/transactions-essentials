/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

/**
 * Instances of this class can be used to lookup resource transactions in a
 * hashtable that uses the XAResource instance for mapping. This is needed
 * because otherwise the JTA wouldn't work with XAResource implementations that
 * have overridden equals.
 *
 */
class XAResourceKey
{

    private XAResource xares;

    public XAResourceKey ( XAResource xares )
    {
        super ();
        this.xares = xares;
    }

    public boolean equals ( Object o )
    {
        boolean ret = false;
        if ( o instanceof XAResourceKey ) {
            XAResourceKey other = (XAResourceKey) o;
            try {
                ret = (other.xares == xares || other.xares.isSameRM ( xares ));
            } catch ( XAException e ) {
                // just return false
            }
        }

        return ret;
    }

    public int hashCode ()
    {
        return xares.getClass ().getName ().toString ().hashCode ();
    }

    public String toString ()
    {
        return xares.toString ();
    }

}
