/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.Hashtable;

import javax.naming.BinaryRefAddr;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 *
 * A default object factory for serializable objects that
 * need to be bound in JNDI.
 */

public class SerializableObjectFactory implements ObjectFactory
{

	static Reference createReference ( Serializable object , String factoryClassName )
	throws NamingException
	{
		Reference ret = null;
		BinaryRefAddr handle = null;
		ByteArrayOutputStream bout;
        try
        {
            bout = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream ( bout );
            out.writeObject ( object );
            out.close();
        }
        catch (IOException e)
        {
            throw new NamingException ( e.getMessage() );
        }

		handle = new BinaryRefAddr ( "com.atomikos.serializable" , bout.toByteArray() );
		ret = new Reference ( object.getClass().getName() , handle ,
			factoryClassName , null );
		return ret;
	}

	/**
	 * Create a reference for the given (Serializable) object.
	 *
	 * @param object The object to create a reference for.
	 * @return Reference The reference that can be bound
	 * in JNDI and used along with this factory class to
	 * reconstruct the original object.
	 * @exception NamingException On failure.
	 */

	public static Reference createReference ( Serializable object )
	throws NamingException
	{
		return createReference ( object , SerializableObjectFactory.class.getName() );
	}

    /**
     * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
     */

    @SuppressWarnings("rawtypes")
	public Object getObjectInstance(
		Object obj, Name name,
		Context nameCtx,
		Hashtable environment )
        throws Exception
    {
        Object ret = null;
        if ( obj instanceof Reference ) {
        	Reference ref = ( Reference ) obj;
        	RefAddr ra = ref.get ( "com.atomikos.serializable" );
        	if ( ra != null ) {
        		byte[] bytes = ( byte[] ) ra.getContent();
        		ByteArrayInputStream bin = new ByteArrayInputStream ( bytes );
        		ObjectInputStream in = new ObjectInputStream ( bin ) {
					protected Class<?> resolveClass ( ObjectStreamClass desc )
						throws IOException, ClassNotFoundException
					{
						try
						{
							//try default class loading
							return super.resolveClass(desc);
						}
						catch ( ClassNotFoundException ex )
						{
							//try the context class loader - happens in OSGi?
							return Thread.currentThread().getContextClassLoader()
										.loadClass(desc.getName());
						}
					}
				};
        		ret = in.readObject();
        		in.close();
        	}
        }

        return ret;
    }

}
