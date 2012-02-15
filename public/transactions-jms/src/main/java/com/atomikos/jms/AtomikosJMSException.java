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


import javax.jms.JMSException;

import com.atomikos.icatch.system.Configuration;

 /**
  * An extension of the standard JMSException with custom
  * logic for error reporting. 
  */

public class AtomikosJMSException extends JMSException {
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosJMSException.class);
	

	private static final long serialVersionUID = 1L;
	
	
	
	/**
	 * Logs and throws and AtomikosJMSException.
	 * @param msg The message to use.
	 * @param cause The cause. 
	 * @throws AtomikosJMSException
	 */
	public static void throwAtomikosJMSException ( String msg , Throwable cause ) throws AtomikosJMSException 
	{
		LOGGER.logWarning ( msg , cause );
		throw new AtomikosJMSException ( msg , cause );
	}
	
	/**
	 * Logs and throws an AtomikosJMSException.
	 * @param msg The message to use.
	 * @throws AtomikosJMSException
	 */
	
	public static void throwAtomikosJMSException ( String msg ) throws AtomikosJMSException 
	{
		throwAtomikosJMSException ( msg , null );
	}

	AtomikosJMSException(String reason) {
		super(reason);
	}

	AtomikosJMSException(String reason, Throwable t) {
		super(reason);
		initCause(t);
		if ( t instanceof Exception ) 
			setLinkedException ( (Exception) t );
	}


}
