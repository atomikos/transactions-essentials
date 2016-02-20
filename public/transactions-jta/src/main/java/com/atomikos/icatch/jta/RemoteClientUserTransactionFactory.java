/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 *
 *
 * The factory for JNDI lookup of RemoteClientUserTransactionFactory objects.
 */

public class RemoteClientUserTransactionFactory implements ObjectFactory
{


    public RemoteClientUserTransactionFactory ()
    {
    }

    /**
     * @see javax.naming.spi.ObjectFactory
     */

    @SuppressWarnings("rawtypes")
	public Object getObjectInstance ( Object obj , Name name , Context nameCtx ,
            Hashtable environment ) throws Exception
    {
        RemoteClientUserTransaction ret = null;

        if ( obj == null || !(obj instanceof Reference) )
            return null;

        Reference ref = (Reference) obj;
        if ( !ref.getClassName ().equals (
                "com.atomikos.icatch.jta.RemoteClientUserTransaction" ) )
            return null;
        // as required by JNDI
        String jndiName = (String) ref.get ( "ServerName" ).getContent ();
        String url = (String) ref.get ( "ProviderUrl" ).getContent ();
        String initialFactory = (String) ref.get ( "ContextFactory" )
                .getContent ();
        String timeoutString = (String) ref.get ( "Timeout" ).getContent ();
        int timeout = Integer.parseInt ( timeoutString );

        ret = new RemoteClientUserTransaction ( jndiName, initialFactory, url );
        ret.setTransactionTimeout ( timeout );
        return ret;
    }

}
