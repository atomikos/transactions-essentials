package com.atomikos.datasource.xa;

import javax.transaction.xa.Xid;

/**
 * 
 * 
 * A factory for creating new Xid instances. This allows different factories for
 * different resources, which is needed because some resources need a custom Xid
 * format.
 */

public interface XidFactory
{
    /**
     * Creates a new Xid instance for a given composite transaction id and
     * resource name.
     * 
     * @param tid
     *            The unique ID of the composite transaction.
     * @param resourcename
     *            The unique resource name.
     * @return Xid The Xid instance.
     */

    public Xid createXid ( String tid , String resourcename );
}
