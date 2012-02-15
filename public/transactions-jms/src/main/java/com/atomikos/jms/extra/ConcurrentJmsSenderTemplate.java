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

package com.atomikos.jms.extra;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;


import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import com.atomikos.icatch.system.Configuration;


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
 * This class produces instances for concurrent use by different threads:
 * threaded applications can share one instance.
 * <p>
 * <b>Important: if you change any properties AFTER sending on the session, then
 * you will need to explicitly stop and restart the session to have the changes
 * take effect!</b>
 * 
 *
 */


public class ConcurrentJmsSenderTemplate extends AbstractJmsSenderTemplate
{
	private static final Logger LOGGER = LoggerFactory.createLogger(ConcurrentJmsSenderTemplate.class);

	public ConcurrentJmsSenderTemplate()
	{
		 super();
	}
	
	
	
	protected Session getOrRefreshSession ( Connection c ) throws JMSException 
	{
		
		Session ret = null;
		ret = c.createSession ( true , 0 );
		return ret;
	}
	

	
	public String toString() 
	{
		return "AbstractJmsSenderTemplate";
	}


	protected void afterUseWithoutErrors ( Session session ) throws JMSException 
	{
		//close session
		session.close();	
	}



	protected void destroy(Connection c, Session s)
			throws JMSException {
		//close anyway - let pooling do its work!
		afterUseWithoutErrors ( c , s );
		
	}



	protected void afterUseWithoutErrors ( Connection c , Session s)
			throws JMSException {
		
		try {
			if ( s != null ) s.close();
		} catch ( JMSException warn ) {
			LOGGER.logWarning ( this + ": error closing session" , warn);
		}
		
		try {
			if ( c != null ) c.close();
		} catch ( JMSException warn ) {
			LOGGER.logWarning ( this + ": error closing connection" , warn);
		}
		
	}



	protected Connection getOrReuseConnection() throws JMSException {
		return refreshConnection();
	}





	
	

}
