
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

