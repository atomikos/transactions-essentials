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



//Revision 1.2.2.3  2007/01/29 11:44:48  guy
//FIXED 10107
//
//Revision 1.2.2.2  2006/10/13 13:07:04  guy
//ADDED 1010
//
//Revision 1.2.2.1  2006/10/10 14:01:43  guy
//ADDED 1011
//
//Revision 1.2  2006/09/22 09:27:17  guy
//Removed ref to old name: TransactionsJTA
//
//Revision 1.1.1.1  2006/08/29 10:01:13  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:32  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.3  2006/03/21 13:23:21  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.2  2006/03/15 10:32:05  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:15  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/05/10 08:45:00  guy
//Merged-in changes of Transactions_2_03 branch.
//
//Revision 1.3.2.1  2005/02/07 13:53:00  guy
//Added description in exceptions.
//
//Revision 1.3  2004/10/13 14:16:16  guy
//Updated javadoc and added String-based methods for heuristic receiver/sender.
//
//Revision 1.2  2004/10/12 13:04:37  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//
//Revision 1.1.2.2  2002/09/13 09:02:18  guy
//Corrected initial mistakes.
//
//Revision 1.1.2.1  2002/09/12 14:23:58  guy
//Added JMS wrapper classes to make JMS transactional in a transparant way.
//

package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueSender;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.TransactionalResource;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.StringHeuristicMessage;

/**
 *
 *
 * A queue sender wrapper that enlists/delists before and after sends.
 */

class JtaQueueSender extends DefaultJtaMessageProducer
implements HeuristicQueueSender
{


    /**
     * Create a new instance.
     *
     * @param sender
     *            The sender to wrap.
     * @param res
     *            The resource to use.
     * @param xares
     *            The XAResource
     */

    JtaQueueSender ( QueueSender sender , TransactionalResource res ,
            XAResource xares )
    {
        super ( sender , res , xares );
    }

    private QueueSender getQueueSender()
    {
    		return ( QueueSender ) getMessageProducer();
    }

    /**
     * @see HeuristicQueueSender
     */

    public void send ( Message msg , HeuristicMessage hmsg )
            throws JMSException
    {

        sendToDefaultDestination ( msg , hmsg );
    }

    /**
     * @see HeuristicQueueSender
     */

    public void send ( Message msg , int deliveryMode , int priority ,
            long timeToLive , HeuristicMessage hmsg ) throws JMSException
    {
    	 	sendToDefaultDestination ( msg , deliveryMode , priority , timeToLive , hmsg );
    }

    /**
     * @see HeuristicQueueSender
     */

    public void send ( Queue q , Message msg , HeuristicMessage hmsg )
            throws JMSException
    {
        sendToDestination ( q , msg , hmsg );
    }

    /**
     * @see HeuristicQueueSender
     */

    public void send ( Queue q , Message msg , int deliveryMode , int priority ,
            long timeToLive , HeuristicMessage hmsg ) throws JMSException
    {
        sendToDestination ( q , msg , deliveryMode , priority , timeToLive, hmsg );
    }

    /**
     * @see QueueSender
     */

    public void send ( Queue q , Message msg ) throws JMSException
    {
        HeuristicMessage hmsg = new StringHeuristicMessage (
                "Sending of JMS Message with ID: " + msg.getJMSMessageID () );
        sendToDestination ( q , msg , hmsg );
    }

    /**
     * @see QueueSender
     */

    public void send ( Queue q , Message msg , int deliveryMode , int priority ,
            long timeToLive ) throws JMSException
    {
        HeuristicMessage hmsg = new StringHeuristicMessage (
                "Sending of JMS Message with ID: " + msg.getJMSMessageID () );
        sendToDestination ( q , msg , deliveryMode , priority , timeToLive, hmsg );
    }

    /**
     * @see QueueSender
     */

    public Queue getQueue () throws JMSException
    {
        return getQueueSender().getQueue ();
    }

    //
    // BELOW IS IMPLEMENTATION OF JMS MESSAGEPRODUCER
    //



    public void send ( Message msg , String hmsg ) throws JMSException
    {
        HeuristicMessage heurmsg = new StringHeuristicMessage ( hmsg );
        send ( msg, heurmsg );

    }

    public void send ( Message msg , int deliveryMode , int priority ,
            long timeToLive , String hmsg ) throws JMSException
    {
        HeuristicMessage heurmsg = new StringHeuristicMessage ( hmsg );
        send ( msg, deliveryMode, priority, timeToLive, heurmsg );

    }

    public void send ( Queue q , Message msg , String hmsg )
            throws JMSException
    {
        HeuristicMessage heurmsg = new StringHeuristicMessage ( hmsg );
        send ( q, msg, heurmsg );

    }

    public void send ( Queue q , Message msg , int deliveryMode , int priority ,
            long timeToLive , String hmsg ) throws JMSException
    {
        HeuristicMessage heurmsg = new StringHeuristicMessage ( hmsg );
        send ( q, msg, deliveryMode, priority, timeToLive, heurmsg );

    }

}
