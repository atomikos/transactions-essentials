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

import java.io.Serializable;
import java.util.Enumeration;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageEOFException;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import com.atomikos.jms.AbstractBridge;

/**
 * 
 * 
 * 
 * A default bridge implementation that forwards JMS messages from the source to
 * the destination. Instances can be used to bridge different JMS providers. The
 * destination is set explicitly as a Queue(Topic)SenderSessionFactory, whereas the
 * source is set implicitly by setting a bridge instance as the MessageListener
 * to a Queue(Topic)ReceiverSession or Queue(Topic)ReceiverSessionPool.
 * 
 * <p>
 * Note that the replyTo header is NOT passed over the brige. Instead, we
 * recommend configuring a <b>dedicated</b> replyTo queue (topic) at a well-known
 * location, and configuring a <b>second bridge</b> that connects the replyTo
 * channels in both interconnected JMS provider domains.
 * 
 * For request/reply across JMS vendor domains X and Y, we therefore recommend
 * the following configuration:
 * 
 * <p>
 * For the <b>request channel</b>:
 * <ul>
 * <li>A request queue (topic) in domain X</li>
 * <li>A Queue(Topic)ReceiverSessionPool that listens on this request queue (topic)</li>
 * <li>A Bridge that uses this session pool as its source, and that has as its
 * destination:</li>
 * <li>A Queue(Topic)SenderSessionFactory that sends messages to the corresponding
 * request queue (topic) in domain Y</li>
 * </ul>
 * 
 * <p>
 * For the <b>reply channel</b>:
 * <ul>
 * <li>A reply queue (topic) in domain Y, <b>also set as the replyToQueue(Topic) property of
 * the request channel bridge</b></li>
 * <li>A Queue(Topic)ReceiverSessionPool that listens on this reply queue (topic)</li>
 * <li>A Bridge that uses this session pool as its source, and that has as its
 * destination:</li>
 * <li>A Queue(Topic)SenderSessionFactory that sends messages to the corresponding
 * reply queue (topic) in domain X</li>
 * </ul>
 * 
 * <p>
 * In order to close all resources, it is recommended that the receiver session
 * configuration is set to notify the listener (the bridge, in this case) when
 * the session is closed. See the notifyListenerOnClose property of the
 * Queue(Topic)ReceiverSession(Pool).
 */

public class Bridge extends AbstractBridge
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(Bridge.class);

    protected Message bridgeMessage ( Message message ) throws JMSException
    {
        Message ret = null;

        if ( message instanceof TextMessage ) {
            String text = ((TextMessage) message).getText ();

            TextMessage m = createTextMessage ();
            m.setText ( text );
            ret = m;
        } else if ( message instanceof MapMessage ) {
            MapMessage m = createMapMessage ();
            MapMessage src = (MapMessage) message;
            Enumeration names = src.getMapNames ();
            while ( names.hasMoreElements () ) {
                String name = (String) names.nextElement ();
                Object val = src.getObject ( name );
                m.setObject ( name, val );
            }
            ret = m;
        } else if ( message instanceof ObjectMessage ) {
            ObjectMessage m = createObjectMessage ();
            Serializable content = ((ObjectMessage) message).getObject ();
            m.setObject ( content );
            ret = m;
        } else if ( message instanceof StreamMessage ) {
            StreamMessage m = createStreamMessage ();
            StreamMessage src = (StreamMessage) message;
            Object lastRead = null;
            do {
                lastRead = null;
                try {
                    lastRead = src.readObject ();
                    if ( lastRead != null ) {
                        m.writeObject ( lastRead );
                        // System.out.println ( "Copied object: " + lastRead );
                        // System.out.println ( "to message: " + m );
                    }
                } catch ( MessageEOFException noMoreData ) {
                }
            } while ( lastRead != null );

            ret = m;
        } else if ( message instanceof BytesMessage ) {
            BytesMessage m = createBytesMessage ();
            BytesMessage src = (BytesMessage) message;
            final int LEN = 100;
            byte[] buf = new byte[LEN];
            int bytesRead = src.readBytes ( buf );
            while ( bytesRead >= 0 ) {
                m.writeBytes ( buf, 0, bytesRead );
                bytesRead = src.readBytes ( buf );
            }

            ret = m;
        } else {
            // no known subinterface of Message
            // this can happen if headers-only message (see Session's creation
            // methods!)
            ret = createTextMessage ();
            // do nothing, merely copy headers below
        }

        copyHeadersAndProperties ( message, ret );

        return ret;
    }

}
