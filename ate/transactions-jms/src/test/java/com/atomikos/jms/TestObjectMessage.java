package com.atomikos.jms;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

/**
 * 
 * 
 * 
 * 
 *
 * 
 */
public class TestObjectMessage extends TestMessage implements ObjectMessage
{

	private Serializable object;
   
    public Serializable getObject() throws JMSException
    {
        return object;
    }

   
    public void setObject(Serializable obj ) throws JMSException
    {
        this.object = obj;

    }

   
    public void clearBody() throws JMSException
    {
        setObject ( null );

    }

}
