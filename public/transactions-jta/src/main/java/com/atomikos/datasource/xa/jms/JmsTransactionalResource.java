/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa.jms;

import javax.jms.JMSException;
import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;
import javax.jms.XASession;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.ResourceException;
import com.atomikos.datasource.xa.XATransactionalResource;

/**
 *
 * resource implementation for JMS queues.
 *
 *
 */

public class JmsTransactionalResource extends XATransactionalResource
{

    private XAConnectionFactory factory;

    private XAConnection conn;

    /**
     * Create a new instance.
     *
     * @param name
     *            The unique resource name.
     * @param factory
     *            The xa connection factory to use.
     */

    public JmsTransactionalResource ( String name , XAConnectionFactory factory )
    {
        super ( name );
        this.factory = factory;
        this.conn = null;
    }

    /**
     * Implements the functionality to get an XAResource handle.
     *
     * @return XAResource The XAResource instance.
     */

    @Override
	protected synchronized XAResource refreshXAConnection ()
            throws ResourceException
    {
        XAResource res = null;

        if ( this.conn != null ) {
            try {
                this.conn.close ();
            } catch ( Exception err ) {
                // happens if connection has timed out
                // which is probably normal, otherwise
                // refresh would not be called in the first place
            }
        }

        try {
            this.conn = this.factory.createXAConnection ();
            XASession session = this.conn.createXASession ();
            // note: session does not have to be kept in attribute,
            // since JMS explicitly states that closing the connection
            // also closes all sessions.
            res = session.getXAResource ();
        } catch ( JMSException jms ) {
            throw new ResourceException ( "Error in getting XA resource",
            		jms );
        }

        return res;

    }

    /**
     * Overrides default close to include closing any open connections to the
     * JMS infrastructure.
     */

    @Override
	public void close () throws ResourceException
    {
        super.close ();
        try {
            if ( this.conn != null ) this.conn.close ();
        } catch ( JMSException err ) {
            throw new ResourceException ( err.getMessage () );
        }
    }

}
