//$Id: TestStreamMessage.java,v 1.1.1.1 2006/08/29 10:01:14 guy Exp $
//$Log: TestStreamMessage.java,v $
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.jms.JMSException;
import javax.jms.MessageEOFException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;
import javax.jms.StreamMessage;

/**
 * 
 * 
 * 
 * 
 *
 * 
 */
public class TestStreamMessage extends TestMessage implements StreamMessage
{
	

	private boolean readMode = false;
	
	private ObjectOutputStream out;
	private ByteArrayOutputStream bout;
	private ObjectInputStream in;
	private ByteArrayInputStream bin;
	
	public TestStreamMessage() throws JMSException 
	{
		bout = new ByteArrayOutputStream();
		try
        {
            out = new ObjectOutputStream ( bout );
        }
        catch (IOException e)
        {
            throw new JMSException ( e.getMessage() );
        }
	}
	
	private void assertWriteable()
	throws JMSException
	{
		if ( readMode )
			throw new MessageNotWriteableException("");
	}

	private void assertReadable()
	throws JMSException
	{
		if ( ! readMode )
			throw new MessageNotReadableException ( "");
	}
  
    public boolean readBoolean() throws JMSException
    {
        throw new JMSException ( "Not Implemented");
    }

  
    public byte readByte() throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

    public int readBytes(byte[] array) throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

   
    public char readChar() throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

   
    public double readDouble() throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

    
    public float readFloat() throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

    
    public int readInt() throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

   
    public long readLong() throws JMSException
    {	
		throw new JMSException ( "Not Implemented");
    }

   
    public Object readObject() throws JMSException
    {
       Object ret = null;
       assertReadable();
//       try
//    {
//        if ( in.available() <= 0 )
//           	throw new MessageEOFException ( "No more to read" );
//    }
//    catch (IOException e)
//    {
//       throw new MessageEOFException ( e.getMessage() );
//    }
       try
    {
    	//System.out.println ( "TestStreamMessage " + this + ": reading...");
        ret = in.readObject();
        //System.out.println ( "TestStreamMessage: read object " + ret );
    }
    catch (Exception e)
    {
        throw new MessageEOFException ( "No more to read" );
    }
       return ret;
    }

   
    public short readShort() throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

    
    public String readString() throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }

    
    public void reset() throws JMSException
    {
    	if ( out == null ) throw new JMSException ( "Can't reset in read mode" );
		try
        {
            out.close();
        }
        catch (IOException e)
        {
            throw new JMSException (  e.getMessage() );
        }
		byte[] bytes = bout.toByteArray();
		//System.out.println ( "TestStreamMessage: containing " + bytes.length + " bytes");
		
        readMode = true;
        bout = null;
        out = null;
        
        bin = new ByteArrayInputStream ( bytes );
        try
        {
            in = new ObjectInputStream ( bin );
        }
        catch (IOException e)
        {
            throw new JMSException ( e.getMessage() );
        }

    }

    public void writeBoolean(boolean arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

   
    public void writeByte(byte arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

    public void writeBytes(byte[] arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

    
    public void writeBytes(byte[] arg0, int arg1, int arg2) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

    
    public void writeChar(char arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

    
    public void writeDouble(double arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

   
    public void writeFloat(float arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

    
    public void writeInt(int arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

    
    public void writeLong(long arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

    
    public void writeObject(Object obj) throws JMSException
    {
        assertWriteable();
        try
        {
            out.writeObject(obj);
        }
        catch (IOException e)
        {
            throw new JMSException ( e.getMessage() );
        }

    }

    
    public void writeShort(short arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

  
    public void writeString(String arg0) throws JMSException
    {
		throw new JMSException ( "Not Implemented");

    }

   
    public void clearBody() throws JMSException
    {
        readMode = false;
        try
        {
            if ( in != null ) in.close();
            in = null;
            bin = null;
            
            bout = new ByteArrayOutputStream();
            out = new ObjectOutputStream ( bout );
        }
        catch (Exception e)
        {
            throw new JMSException ( e.getMessage() );
        }

    }

}
