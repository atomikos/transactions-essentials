package com.atomikos.jdbc.nonxa;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * 
 * 
 * 
 * 
 * 
 * 
 */
public class NonXADataSourceImpFactory implements ObjectFactory
{

    public Object getObjectInstance ( Object obj , Name name , Context nameCtx ,
            Hashtable environment ) throws Exception
    {
        if ( !(obj instanceof Reference) )
            return null;

        Reference ref = (Reference) obj;
        if ( !ref.getClassName ().equals (
                "com.atomikos.jdbc.nonxa.NonXADataSourceImp" ) )
            return null;
        // as required by JNDI

        String url = (String) ref.get ( "ResourceName" ).getContent ();

        return NonXADataSourceImp.getInstance ( url );

    }

}
