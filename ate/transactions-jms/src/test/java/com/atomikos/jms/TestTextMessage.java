package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * 
 * 
 * 
 * 
 *
 * 
 */
public class TestTextMessage extends TestMessage implements TextMessage
{
	private String text;
    
    public String getText() throws JMSException
    {
        return text;
    }

    
    public void setText(String text) throws JMSException
    {
        this.text = text;

    }

  
    public void clearBody() throws JMSException
    {
        text = null;

    }

}
