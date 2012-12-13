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

package com.atomikos.datasource.xa.jms;

import java.util.Stack;

import javax.jms.JMSException;
import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;
import javax.jms.XASession;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.ResourceException;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.datasource.xa.XidFactory;

/**
 *
 * resource implementation for JMS queues.
 *
 *
 */

public class JmsTransactionalResource extends XATransactionalResource
{

    private XAConnectionFactory factory_;

    private XAConnection conn_;

    /**
     * Create a new instance.
     *
     * @param name
     *            The unique resource name.
     * @param factory
     *            The xa connection factory to use.
     */

    public JmsTransactionalResource ( String name ,
            XAConnectionFactory factory )
    {
        super ( name );
        factory_ = factory;
        conn_ = null;
    }

    /**
     * Create a new instance, but one that requires a specific Xid format. This
     * may be necessary for some JMS implementations that require their own
     * format.
     *
     * @param name
     *            The unique resource name.
     * @param qFactory
     *            The queue connection factory.
     * @param xidFactory
     *            The factory for Xid instances.
     */

    public JmsTransactionalResource ( String name ,
            XAConnectionFactory qFactory , XidFactory xidFactory )
    {
        super ( name , xidFactory );
        factory_ = qFactory;
        conn_ = null;
    }

    /**
     * Implements the functionality to get an XAResource handle.
     *
     * @return XAResource The XAResource instance.
     */

    protected synchronized XAResource refreshXAConnection ()
            throws ResourceException
    {
        XAResource res = null;

        if ( conn_ != null ) {
            try {
                conn_.close ();
            } catch ( Exception err ) {
                // happens if connection has timed out
                // which is probably normal, otherwise
                // refresh would not be called in the first place
            }
        }

        try {
            conn_ = factory_.createXAConnection ();
            XASession session = conn_.createXASession ();
            // note: session does not have to be kept in attribute,
            // since JMS explicitly states that closing the connection
            // also closes all sessions.
            res = session.getXAResource ();
        } catch ( JMSException jms ) {
            Stack errors = new Stack ();
            errors.push ( jms );
            throw new ResourceException ( "Error in getting XA resource",
                    errors );
        }

        return res;

    }

    /**
     * Overrides default close to include closing any open connections to the
     * JMS infrastructure.
     */

    public void close () throws ResourceException
    {
        super.close ();
        try {
            if ( conn_ != null ) conn_.close ();
        } catch ( JMSException err ) {
            throw new ResourceException ( err.getMessage () );
        }
    }

}
