package com.atomikos.icatch.jta;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

/**
 * 
 * 
 * 
 * Instances of this class can be used to lookup resource transactions in a
 * hashtable that uses the XAResource instance for mapping. This is needed
 * because otherwise the JTA wouldn't work with XAResource implementations that
 * have overridden equals.
 * 
 * 
 * 
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
        // System.out.println ( "EQUALS OF XAResourceKey");
        boolean ret = false;
        if ( o instanceof XAResourceKey ) {
            XAResourceKey other = (XAResourceKey) o;
            // ret = other.xares.toString().equals ( xares.toString() );
            try {
                ret = (other.xares == xares || other.xares.isSameRM ( xares ));
            } catch ( XAException e ) {
                // just return false
            }
            // System.out.println ( "COMPARING XARESOURCES: " + xares + " and "
            // + other.xares );
            // System.out.println ( "RESULT OF COMPARISON: " + ret );
        }

        return ret;
    }

    public int hashCode ()
    {
        // System.out.println ( "hashCode OF XAResourceKey: returning " +
        // xares.toString().hashCode());
        return xares.getClass ().getName ().toString ().hashCode ();
    }

    public String toString ()
    {
        return xares.toString ();
    }

}
