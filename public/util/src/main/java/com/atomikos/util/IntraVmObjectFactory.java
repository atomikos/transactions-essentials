/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.util;

import java.io.Serializable;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;



/**
 * An intra-VM object factory. Objects registered in the
 * IntraVmObjectRegistry can be restored from a reference
 * with the help of this factory.
 *
 *
 */

public class IntraVmObjectFactory implements ObjectFactory
{

	private static final String NAME_REF_ADDRESS_TYPE = "uniqueResourceName";

	private static SerializableObjectFactory serializableObjectFactory = new SerializableObjectFactory();

	//synchronized static to avoid race conditions between concurrent retrievals
	@SuppressWarnings("rawtypes")
	private static synchronized Object retrieveObjectInstance (
			Object obj, Name name, Context nameCtx, Hashtable environment)
			throws Exception
	{
		Object  ret = null;


		if ( obj instanceof Reference ) {
			String resourceName = null;
			Reference ref = ( Reference ) obj;
			StringRefAddr nameAsRefAddr = ( StringRefAddr ) ref.get ( NAME_REF_ADDRESS_TYPE );
			resourceName = (String) nameAsRefAddr.getContent();
			try {
				ret = IntraVmObjectRegistry.getResource ( resourceName );
			} catch ( NameNotFoundException notYetInited ) {
				//not registered in this VM -> init and return the deserialized instance
				Object resource = serializableObjectFactory.getObjectInstance(obj, name, nameCtx, environment);
				IntraVmObjectRegistry.addResource ( resourceName, resource );
				ret = resource;
			}
		}

		return ret;
	}

	public static synchronized Reference createReference ( Serializable object , String name ) throws NamingException
	{
		Reference ret = null;
		if ( object == null ) throw new IllegalArgumentException ( "invalid resource: null" );
		if ( name == null ) throw new IllegalArgumentException ( "name should not be null" );

		//make sure that lookup works - add the bean to the registry if needed
		try {
			Object existing = IntraVmObjectRegistry.getResource ( name );
			if ( existing != object ) {
				//another instance with the same name already there
				String msg = "Another resource already exists with name " + name + " - pick a different name";
				throw new NamingException ( msg );
			}
		} catch ( NameNotFoundException notThere ) {
			// make sure this bean is registered for JNDI lookups to find the same instance
			// otherwise, concurrent lookups would create race conditions during init
			// and the thread that creates a bean might not be able to use it (unfair?)
			IntraVmObjectRegistry.addResource ( name , object );
		}
		ret = SerializableObjectFactory.createReference ( object , IntraVmObjectFactory.class.getName() );
		//also add the unique resource name for helping during retrieval
		ret.add ( new StringRefAddr ( NAME_REF_ADDRESS_TYPE , name ) );
		return ret;
	}


	@SuppressWarnings("rawtypes")
	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment) throws Exception
	{
		return retrieveObjectInstance(obj, name, nameCtx, environment);
	}

}
