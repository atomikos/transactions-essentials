package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.TransactionalResource;

/**
 * 
 * 
 * A queue receiver with heuristic info.
 */

class JtaQueueReceiver 
extends DefaultJtaMessageConsumer
implements HeuristicQueueReceiver
{
  

    JtaQueueReceiver ( QueueReceiver receiver , TransactionalResource res ,
            XAResource xares )
    {
        super ( receiver , res , xares );
    }

    private QueueReceiver getReceiver()
    {
    		return ( QueueReceiver ) getMessageConsumer();
    }
    
    public Queue getQueue () throws JMSException
    {
        return getReceiver().getQueue ();
    }


}
