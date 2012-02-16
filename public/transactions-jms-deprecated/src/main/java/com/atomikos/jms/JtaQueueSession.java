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
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(JtaQueueSession.class);
   
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

