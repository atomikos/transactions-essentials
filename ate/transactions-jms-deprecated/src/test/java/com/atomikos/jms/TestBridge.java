package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Message;

import com.atomikos.jms.AbstractBridge;

/**
 * 
 * 
 * 
 * 
 * A bridge for testing: bridge generates failures each time.
 * 
 */
public class TestBridge extends AbstractBridge
{

    /* (non-Javadoc)
     * @see com.atomikos.jms.AbstractBridge#bridgeMessage(javax.jms.Message)
     */
    protected Message bridgeMessage(Message message) throws JMSException
    {
        throw new JMSException ( "Simulated error");
    }

}
