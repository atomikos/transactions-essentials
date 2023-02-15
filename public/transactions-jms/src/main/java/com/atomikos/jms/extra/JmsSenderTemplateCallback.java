/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.extra;

import javax.jms.JMSException;
import javax.jms.Session;

 /**
  * This is a call-back interface for doing more advanced
  * and non-standard processing with the JmsSenderTemplate classes.
  * 
  * Application code can implement this interface to get full access
  * to the underlying (and managed!) JMS Session object.
  * 
  */

@FunctionalInterface
public interface JmsSenderTemplateCallback
{

	/**
	 * Performs some application-specific processing on the
	 * underlying JMS session.
	 * 
	 * @param session The JMS session, as managed by the JmsSenderTemplate classes.
	 * @throws JMSException On errors.
	 */
	
	public void doInJmsSession ( Session session ) throws JMSException;
}
