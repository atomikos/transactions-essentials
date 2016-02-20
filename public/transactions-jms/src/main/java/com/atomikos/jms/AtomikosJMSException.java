/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms;

import javax.jms.JMSException;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

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
