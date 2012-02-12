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
