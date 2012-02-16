/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.jms;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

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
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(JndiObjectFactory.class);

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
