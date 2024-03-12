/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
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


	protected Connection getOrReuseConnection() throws JMSException {
		return refreshConnection();
	}



	@Override	
protected void afterUseWithoutErrors(Connection c, Session s)
			throws JMSException {
		// close anyway: pooling will do the reuse
		destroy(c,s);
		
	}





	
	

}
