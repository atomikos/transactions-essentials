package com.atomikos.jms;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * 
 * 
 * An object factory for JMS administrated objects.
 */

public class JndiObjectFactory implements ObjectFactory
{
    public JndiObjectFactory ()
    {
    }

    public Object getObjectInstance ( Object obj , Name name , Context nameCtx ,
            Hashtable environment ) throws Exception
    {
    		Object ret = null;
        if ( !(obj instanceof Reference) )
            return null;
        
        Reference ref = (Reference) obj;
        String className = ref.getClassName();
        if ( JtaQueueConnectionFactory.class.getName ().equals ( className ) ) {
        		String url = (String) ref.get ( "ResourceName" ).getContent ();
        		ret = JtaQueueConnectionFactory.getInstance ( url );
        }   
        else if ( JtaTopicConnectionFactory.class.getName ().equals ( className ) ) {
        		String url = (String) ref.get ( "ResourceName" ).getContent ();
        		ret = JtaTopicConnectionFactory.getInstance ( url );
        }
        else {
        		ret = null;
        		//as required by JNDI
        }
        
        return ret;
    }

}
