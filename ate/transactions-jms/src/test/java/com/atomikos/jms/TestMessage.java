package com.atomikos.jms;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

/**
 * 
 * 
 * 
 * 
 * A test message implementation.
 * 
 */

public abstract class TestMessage implements Message
{

	private boolean acknowledge = false;
	
	private Hashtable properties = new Hashtable();
	
	private String correlationId = null;
	
	private int deliveryMode;
	
	private Destination destination;
	
	private long expiration;
	
	private String  messageId;
	
	private int priority;
	
	private Destination replyTo;
	
	private long timestamp;
	
	private String type;
	
	private boolean redelivered = false;
    
    public void acknowledge() throws JMSException
    {
        acknowledge = true;

    }

	public boolean wasAcknowledgeCalled()
	{
		return acknowledge;
	}
   

    public void clearProperties() throws JMSException
    {
        properties = new Hashtable();

    }

    public boolean getBooleanProperty(String name) throws JMSException
    {
        	throw new JMSException ( "Not Implemented");
        	
    }

    
    public byte getByteProperty(String arg0) throws JMSException
    {
       		throw new JMSException ( "Not Implemented");
    }

   
    public double getDoubleProperty(String arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

    public float getFloatProperty(String arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

    public int getIntProperty(String arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

   
    public String getJMSCorrelationID() throws JMSException
    {
        return correlationId;
    }

    public byte[] getJMSCorrelationIDAsBytes() throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

  
    public int getJMSDeliveryMode() throws JMSException
    {
       
        return deliveryMode;
    }

   
    public Destination getJMSDestination() throws JMSException
    {
        return destination;
    }

   
    public long getJMSExpiration() throws JMSException
    {
        return expiration;
    }

    
    public String getJMSMessageID() throws JMSException
    {
        return messageId;
    }

  
    public int getJMSPriority() throws JMSException
    {
        return priority;
    }

    
    public boolean getJMSRedelivered() throws JMSException
    {
      
        return redelivered;
    }

    
    public Destination getJMSReplyTo() throws JMSException
    {
        return replyTo;
    }

    
    public long getJMSTimestamp() throws JMSException
    {
       return timestamp;
    }


    public String getJMSType() throws JMSException
    {
        return type;
    }

   
    public long getLongProperty(String arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

   
    public Object getObjectProperty(String arg0) throws JMSException
    {
        return properties.get ( arg0 );
    }

   
    public Enumeration getPropertyNames() throws JMSException
    {
    	Enumeration ret = properties.keys();
    	return ret;
    }

    public short getShortProperty(String arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

    
    public String getStringProperty(String arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

   
    public boolean propertyExists(String arg0) throws JMSException
    {
        return properties.get(arg0) != null;
    }

    
    public void setBooleanProperty(String arg0, boolean arg1)
        throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

  
    public void setByteProperty(String arg0, byte arg1) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

  
    public void setDoubleProperty(String arg0, double arg1) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

  
    public void setFloatProperty(String arg0, float arg1) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

 
    public void setIntProperty(String arg0, int arg1) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

    public void setJMSCorrelationID(String id) throws JMSException
    {
        this.correlationId = id;

    }

   
    public void setJMSCorrelationIDAsBytes(byte[] arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

 
    public void setJMSDeliveryMode(int mode) throws JMSException
    {
        this.deliveryMode = mode;

    }

   
    public void setJMSDestination(Destination dest) throws JMSException
    {
        this.destination = dest;

    }

   
    public void setJMSExpiration(long expiration) throws JMSException
    {
        this.expiration = expiration;

    }

   
    public void setJMSMessageID(String id) throws JMSException
    {
        this.messageId = id;

    }

    
    public void setJMSPriority(int pty) throws JMSException
    {
       this.priority = pty;

    }

  
    public void setJMSRedelivered(boolean redelivered) throws JMSException
    {
        this.redelivered = redelivered;

    }

   
    public void setJMSReplyTo(Destination replyTo) throws JMSException
    {
        this.replyTo =  replyTo;

    }

    
    public void setJMSTimestamp(long timestamp) throws JMSException
    {
        this.timestamp = timestamp;

    }

  
    public void setJMSType(String type) throws JMSException
    {
        this.type = type;

    }

   
    public void setLongProperty(String arg0, long arg1) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

  
    public void setObjectProperty(String name, Object val) throws JMSException
    {
        properties.put (name ,val );

    }

  
    public void setShortProperty(String arg0, short arg1) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

 
    public void setStringProperty(String arg0, String arg1) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

}
