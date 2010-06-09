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

import javax.jms.ConnectionConsumer;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.XATopicConnection;
import javax.jms.XATopicSession;

import com.atomikos.datasource.TransactionalResource;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * A topic connection with JTA capabilities.
 * <p>
 * Topic functionality in this product was sponsored by <a href="http://www.webtide.com">Webtide</a>.
 */

class JtaTopicConnection extends DefaultJtaConnection implements
		TopicConnection 
{

	
	
	protected JtaTopicConnection ( XATopicConnection c, TransactionalResource res) 
	{
		super(c, res);
	}

	private XATopicConnection getTopicConnection()
	{
		return ( XATopicConnection ) getConnection();
	}
	
	public TopicSession createTopicSession ( boolean transacted , int ackMode )
			throws JMSException 
	{
		TopicSession ret = null;
        if ( !transacted && !inJtaTransaction() ) {
            ret = getTopicConnection().createTopicSession ( false, ackMode );
            // TODO test non-tx mode
        } else {
        	forceConnectionIntoXaMode ( getConnection() );
            XATopicSession xasession = getTopicConnection().createXATopicSession ();
            ret = new JtaTopicSession ( xasession, getTransactionalResource() ,
                    xasession.getXAResource () );
        }

        return ret;
	}

	public ConnectionConsumer createConnectionConsumer (
			Topic topic , String string ,
			ServerSessionPool pool , int value ) throws JMSException {
		//TODO check if we need to implement this
		throw new JMSException ( "Not supported" );
	}

}

