//$Id: Bridge.java,v 1.2 2006/10/30 10:37:09 guy Exp $
//$Log: Bridge.java,v $
//Revision 1.2  2006/10/30 10:37:09  guy
//Merged in changes of 3.1.0 release
//
//Revision 1.1.1.1.4.1  2006/10/20 15:09:57  guy
//Corrected javadoc
//
//Revision 1.1.1.1  2006/08/29 10:01:12  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:31  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:32:04  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:15  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2005/05/11 10:41:24  guy
//Updated javadoc.
//
//Revision 1.1  2005/01/07 17:07:18  guy
//Added JMS receiver support (lightweigh MDB), and JMS queue bridging.
//
package com.atomikos.jms;

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
