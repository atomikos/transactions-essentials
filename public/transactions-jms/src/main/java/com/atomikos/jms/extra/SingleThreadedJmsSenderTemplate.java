/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.extra;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

/**
 * This is a <b>long-lived</b> JMS sender session, representing a
 * self-refreshing JMS session that can be used to send JMS messages in a
 * transacted way (a JTA transaction context is required). 
 * 
 * The client code does not have to worry about refreshing or
 * closing JMS objects explicitly: this is all handled in this class. All the
 * client needs to do is indicate when it wants to start or stop using the
 * session.
 * <p>
 * Note that instances are not meant for concurrent use by different threads:
 * threaded applications should use the {@link ConcurrentJmsSenderTemplate} instead.
 * <p>
 * <b>Important: if you change any properties AFTER sending on the session, then
 * you will need to explicitly stop and restart the session to have the changes
 * take effect!</b>
 */
@SuppressWarnings("WeakerAccess")
public class SingleThreadedJmsSenderTemplate extends AbstractJmsSenderTemplate
{
	private Session session;
	private Connection connection;
	
	public SingleThreadedJmsSenderTemplate()
	{
		super();
	}

	@Override
	protected Session getOrRefreshSession ( Connection c ) throws JMSException 
	{
		//just reuse the prepared session
		return session;
	}

	@Override
	public String toString() 
	{
		return "SingleThreadedJmsSenderTemplate";
	}

	@SuppressWarnings("unused")
  protected void afterUseWithoutErrors ( Session session ) throws JMSException {
		//do nothing here: reuse session next time
	}

	@Override
	protected void afterUseWithoutErrors ( Connection c, Session s )
			throws JMSException {
		//reuse connection and session next time	
	}

	@Override
	protected Connection getOrReuseConnection() throws JMSException 
	{
		if ( connection == null ) {
			connection = refreshConnection();
			session = connection.createSession ( true , 0 );
		}
		return connection;
	}
	
	@Override
	public void destroy ( Connection c , Session s ) throws JMSException {
		super.destroy(c,s);
		this.connection = null;
		this.session = null;
	}
}
