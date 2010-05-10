//$Id: TestMapMessage.java,v 1.1.1.1 2006/08/29 10:01:14 guy Exp $
//$Log: TestMapMessage.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:14  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:57  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:21  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.1  2005/01/07 17:07:31  guy
//Added tests for JMS receiver support (lightweigh MDB), and JMS queue bridging.
//
package com.atomikos.jms;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * 
 * 
 * 
 * 
 *
 * 
 */
public class TestMapMessage extends TestMessage implements MapMessage
{

	private HashMap content = new HashMap();
    
    public boolean getBoolean(String arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

    public byte getByte(String arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

   
    public byte[] getBytes(String arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

    public char getChar(String arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

   
    public double getDouble(String arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

    
    public float getFloat(String arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

  
    public int getInt(String arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

 
    public long getLong(String arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

   
    public Enumeration getMapNames() throws JMSException
    {
       Vector v = new Vector();
       Iterator keys = content.keySet().iterator();
       while ( keys.hasNext() ) {
       		Object key = keys.next();
       		v.add( key );
       }
       return v.elements();
    }

   
    public Object getObject(String name) throws JMSException
    {
        return content.get( name );
    }

   
    public short getShort(String arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

   
    public String getString(String arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

    
    public boolean itemExists(String name) throws JMSException
    {
        return content.containsKey( name );
    }

   
    public void setBoolean(String arg0, boolean arg1) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

   
    public void setByte(String arg0, byte arg1) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

    
    public void setBytes(String arg0, byte[] arg1) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

   
    public void setBytes(String arg0, byte[] arg1, int arg2, int arg3)
        throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

   
    public void setChar(String arg0, char arg1) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

  
    public void setDouble(String arg0, double arg1) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

   
    public void setFloat(String arg0, float arg1) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

   
    public void setInt(String arg0, int arg1) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

   
    public void setLong(String arg0, long arg1) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

    
    public void setObject(String name , Object val ) throws JMSException
    {
 		content.put ( name , val );

    }

  
    public void setShort(String arg0, short arg1) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

   
    public void setString(String arg0, String arg1) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

  
    public void clearBody() throws JMSException
    {
        content = new HashMap();

    }

}
