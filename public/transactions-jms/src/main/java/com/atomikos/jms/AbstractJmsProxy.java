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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.jms.JMSException;

import com.atomikos.icatch.system.Configuration;

 /**
  * Common logic for the different dynamic JMS proxies.
  *
  */
abstract class AbstractJmsProxy implements InvocationHandler
{
	/**
	 * Logger for this class
	 */
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
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( msg , ite );
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
