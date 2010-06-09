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

import java.util.Enumeration;
import java.util.Stack;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import com.atomikos.icatch.SysException;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * 
 * An abstract superclass for transactional destination bridging between a receiver
 * and a sender destination. The destination is set explicitly as a
 * MessageProducerSessionFactory, whereas the source is set implicitly by setting a
 * bridge instance as the MessageListener to a Queue(Topic)ReceiverSession or
 * Queue(Topic)ReceiverSessionPool.
 * 
 * Subclasses should implement the bridgeMessage method to convert the JMS
 * message format from the source session to the destination.
 * 
 * 
 */

// TODO document which sendersession properties are overridden by those from the
// bridged message
public abstract class AbstractBridge implements MessageListener
{

    private MessageProducerSessionFactory destinationFactory;
    private ThreadLocal destinationMap;

    protected AbstractBridge ()
    {
        destinationMap = new ThreadLocal ();
    }

    private MessageProducerSession getDestination ()
    {
        MessageProducerSession ret = null;
        ret = ( MessageProducerSession ) destinationMap.get ();
        if ( ret == null ) {
            ret = destinationFactory.createMessageProducerSession();
            destinationMap.set ( ret );
        }
        return ret;
    }

    /**
     * Set the destination session factory; this is a sender session factory
     * that connects to the destination in the destination domain.
     * 
     * @param destinationFactory
     */
    public void setDestinationSessionFactory (
            MessageProducerSessionFactory destinationFactory )
    {
        this.destinationFactory = destinationFactory;
    }
    


    /**
     * Create a new text message. Subclasses can use this method to create a
     * text message for the destination.
     * 
     * @return
     * @throws JMSException
     */

    protected TextMessage createTextMessage () throws JMSException
    {
        TextMessage ret = null;
        ret = getDestination ().createTextMessage ();
        return ret;
    }

    /**
     * Create a new bytes message. Subclasses can use this method to create a
     * bytes message for the destination.
     * 
     * @return
     * @throws JMSException
     */

    protected BytesMessage createBytesMessage () throws JMSException
    {
        BytesMessage ret = null;
        ret = getDestination ().createBytesMessage ();
        return ret;
    }

    /**
     * Create a new stream message. Subclasses can use this method to create a
     * stream message for the destination.
     * 
     * @return
     * @throws JMSException
     */
    protected StreamMessage createStreamMessage () throws JMSException
    {
        StreamMessage ret = null;
        ret = getDestination ().createStreamMessage ();
        return ret;
    }

    /**
     * Create a new object message. Subclasses can use this method to create an
     * object message for the destination.
     * 
     * @return
     * @throws JMSException
     */
    protected ObjectMessage createObjectMessage () throws JMSException
    {
        ObjectMessage ret = null;
        ret = getDestination ().createObjectMessage ();
        return ret;
    }

    /**
     * Create a new map message. Subclasses can use this method to create a map
     * message for the destination.
     * 
     * @return
     * @throws JMSException
     */

    protected MapMessage createMapMessage () throws JMSException
    {
        MapMessage ret = null;
        ret = getDestination ().createMapMessage ();
        return ret;
    }

    /**
     * Utility method to copy the headers from one message to another.
     * Subclasses can use this method to easily copy the message headers and
     * properties for bridged messages.
     * 
     * @param fromMessage
     *            The incoming message
     * @param toMessage
     *            The bridged message as constructed by the subclass.
     * @throws JMSException
     */
    protected void copyHeadersAndProperties ( Message fromMessage ,
            Message toMessage ) throws JMSException
    {
        Enumeration names = fromMessage.getPropertyNames ();
        while ( names.hasMoreElements () ) {
            String name = (String) names.nextElement ();
            Object val = fromMessage.getObjectProperty ( name );
            toMessage.setObjectProperty ( name, val );
            // System.out.println ( "Copying property " + name + " with value "
            // + val );
        }

        String corrId = fromMessage.getJMSCorrelationID ();
        toMessage.setJMSCorrelationID ( corrId );
        getDestination ().setPriority ( fromMessage.getJMSPriority () );
        long expiry = fromMessage.getJMSExpiration ();
        if ( expiry > 0 ) {
            long now = System.currentTimeMillis ();
            long ttl = expiry - now;
            if ( ttl < 0 )
                throw new JMSException ( "Message has expired" );
            getDestination ().setTimeToLive ( ttl );
        } else {
            // no expiry was set -> keep it that way by using 0
            getDestination ().setTimeToLive ( 0 );
        }

        String type = fromMessage.getJMSType ();
        toMessage.setJMSType ( type );
        getDestination ().setDeliveryMode ( fromMessage.getJMSDeliveryMode () );

        // req/reply should not rely on the replyTo
        // being passed over the bridge

    }

    /**
     * Transform a message into another message. Subclasses should override this
     * method to change the argument message format into the returned message
     * format.
     * 
     * @param message
     *            The message as it comes from the source session (and queue).
     * @return Message The corresponding message to be put on the destination
     *         queue.
     * @throws
     */

    protected abstract Message bridgeMessage ( Message message )
            throws JMSException;

    /**
     * This method is called by the source session when there is an incoming
     * message. The implementation delegates to transformMessage to obtain a
     * converted message that it then sends on to the destination.
     */

    public final void onMessage ( Message message )
    {
        try {
            // System.out.println ( "Bridge.onMessage called!");
            if ( message == null ) {
                // shutdown notification
                Configuration.logInfo ( "Stopping JMS Bridge" );
                // System.out.println ( "Stopping JMS bridge");
                getDestination ().stop ();
            } else {

                Message transformedMsg = bridgeMessage ( message );
                if ( transformedMsg != null ) {
                    // System.out.println ( "Bridging message : " +
                    // transformedMsg + " in thread: " + Thread.currentThread()
                    // );
                    getDestination ().sendMessage ( transformedMsg );

                }
            }
        } catch ( JMSException e ) {
            // e.printStackTrace();
            Configuration.logInfo ( "Bridge: error during message processing", e );
            Configuration.logInfo ( "Bridge: linked exception is " + e.getLinkedException() );
            Stack errors = new Stack ();
            errors.push ( e );
            // throw runtime to rollback
            throw new SysException ( "Error during message processing", errors );
        }
    }

}
