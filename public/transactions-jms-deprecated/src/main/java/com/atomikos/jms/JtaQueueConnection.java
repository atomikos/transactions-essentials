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

package com.atomikos.jms;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import javax.jms.ConnectionConsumer;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.XAQueueConnection;
import javax.jms.XAQueueSession;

import com.atomikos.datasource.TransactionalResource;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * A JTA-aware queueconnection. Instances are passed to the application, but
 * this happens behind the scenes.
 */

class JtaQueueConnection 
extends DefaultJtaConnection
implements QueueConnection
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(JtaQueueConnection.class);

    JtaQueueConnection ( XAQueueConnection c , TransactionalResource res )
    {
        super ( c , res );
    }
    
    private XAQueueConnection getQueueConnection()
    {
    		return ( XAQueueConnection ) getConnection();
    }

    /**
     * Creates a new session for JTA transactions.
     * 
     * @param transacted
     *            If true then XA is used.
     * @param ackMode
     *            The acknowledge mode. (Ignored for transactional sessions.)
     */

    public QueueSession createQueueSession ( boolean transacted , int ackMode )
            throws JMSException
    {
        QueueSession ret = null;
        if ( !transacted && !inJtaTransaction() ) {
            ret = getQueueConnection().createQueueSession ( false, ackMode );
            // TODO test non-tx mode
        } else {
        	forceConnectionIntoXaMode ( getConnection() );
            XAQueueSession xasession = getQueueConnection().createXAQueueSession ();
            ret = new JtaQueueSession ( xasession, getTransactionalResource() ,
                    xasession.getXAResource () );
        }

        return ret;

    }

    public ConnectionConsumer createConnectionConsumer ( Queue queue ,
            String selector , ServerSessionPool pool , int max )
            throws JMSException
    {
        throw new JMSException ( "Not implemented" );
    }

    //
    // FOLLOWING ARE METHODS REQUIRED BY JMS
    // 

    public Session createSession ( boolean transacted , int ackMode ) throws JMSException 
	{
		return createQueueSession ( transacted , ackMode );
	}

}
