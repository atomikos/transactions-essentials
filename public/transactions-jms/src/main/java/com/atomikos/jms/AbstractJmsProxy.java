/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;

import javax.jms.JMSException;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

 /**
  * Common logic for the different dynamic JMS proxies.
  *
  */
abstract class AbstractJmsProxy implements InvocationHandler 
{
	private static final Logger LOGGER = LoggerFactory.createLogger(AbstractJmsProxy.class);

	/**
	 * Converts a driver error (generic exception) into an appropriate 
	 * JMSException or RuntimeException. 
	 * 
	 * @param ex The driver exception.
	 * @param msg The message to use in the logs and conversion.
	 */
	
	protected void convertProxyError ( Throwable ex , String msg ) throws JMSException 
	{	
		if ( ex instanceof Error ) {
			Error err = ( Error ) ex;
			LOGGER.logWarning ( msg , err );
			throw err;
		} else if ( ex instanceof RuntimeException ) {
			RuntimeException rte = ( RuntimeException ) ex;
			LOGGER.logWarning ( msg , ex );
			throw rte;
		} else if ( ex instanceof JMSException ) {
			JMSException driverError = ( JMSException ) ex;
			LOGGER.logWarning ( msg , ex );
			Exception linkedException = driverError.getLinkedException();
			if ( linkedException != null ) LOGGER.logWarning ( "linked exception is " , linkedException );
			throw driverError;
		} else if ( ex instanceof InvocationTargetException ) {
			InvocationTargetException ite = ( InvocationTargetException ) ex;
			Throwable cause = ite.getCause();
			if ( cause != null ) {
				//log as debug and let the convert do the rest for the cause
				if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( msg , ite );
				convertProxyError ( cause , msg );
			}
			else {
				//cause is null -> throw AtomikosJMSException?
				AtomikosJMSException.throwAtomikosJMSException ( msg , ite );
			}
		}
		
		//default: throw AtomikosJMSException
		AtomikosJMSException.throwAtomikosJMSException ( msg , ex );
		
		
	}


}
