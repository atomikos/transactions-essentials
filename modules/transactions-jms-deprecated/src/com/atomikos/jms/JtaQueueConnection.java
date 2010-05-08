package com.atomikos.jms;

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
