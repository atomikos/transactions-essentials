package com.atomikos.datasource.xa;

import javax.transaction.xa.Xid;

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
public abstract class AbstractXidFactory implements XidFactory
{

   //default scope for testing issue 10086
   static long counter = 0;

    // to make sure that XIDs for the same
    // combination of TM,TID are still unique

    protected static void incCounter ()
    {
        counter++;
    }

    protected static long getCounter ()
    {
        return counter;
    }

    public AbstractXidFactory ()
    {
        super ();

    }
    
    /**
     * @see com.atomikos.datasource.xa.XidFactory
     */

    public Xid createXid ( String tid , String resourcename )
    {
        // first increment counter to make sure it is
        // different from the last call that was done
        // by the SAME tid (works because calls within
        // one TID are serial)
        incCounter ();
        return new XID ( tid, resourcename + getCounter () );
    }

}
