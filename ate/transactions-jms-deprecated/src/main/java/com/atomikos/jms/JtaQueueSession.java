package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.XAQueueSession;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.TransactionalResource;

/**
 * 
 * 
 * A special session for Atomikos JTA.
 * <p>
 * Topic functionality in this product was sponsored by 
 * <a href="http://www.webtide.com">Webtide</a>.
 */

class JtaQueueSession 
extends DefaultJtaSession
implements QueueSession
{
   
    JtaQueueSession ( XAQueueSession session , TransactionalResource res ,
            XAResource xares )
    {
        super ( session , res , xares );
    }
    
    private QueueSession getQueueSession()
    {
    		return ( QueueSession ) getSession();
    }

    public QueueSender createSender ( Queue queue ) throws JMSException
    {
        QueueSender sender = getQueueSession().createSender ( queue );
        return new JtaQueueSender ( sender, getTransactionalResource() , getXAResource() );
    }

    public QueueReceiver createReceiver ( Queue q , String selector )
            throws JMSException
    {
        QueueReceiver receiver = getQueueSession().createReceiver ( q, selector );
        return new JtaQueueReceiver ( receiver , getTransactionalResource() , getXAResource() );
    }

    public QueueReceiver createReceiver ( Queue q ) throws JMSException
    {
        QueueReceiver receiver = getQueueSession().createReceiver ( q );
        return new JtaQueueReceiver ( receiver , getTransactionalResource() , getXAResource() );
    }
    


}

