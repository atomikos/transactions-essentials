package com.atomikos.jms;

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
			Configuration.logWarning ( msg , err );
			throw err;
		} else if ( ex instanceof RuntimeException ) {
			RuntimeException rte = ( RuntimeException ) ex;
			Configuration.logWarning ( msg , ex );
			throw rte;
		} else if ( ex instanceof JMSException ) {
			JMSException driverError = ( JMSException ) ex;
			Configuration.logWarning ( msg , ex );
			Exception linkedException = driverError.getLinkedException();
			if ( linkedException != null ) Configuration.logWarning ( "linked exception is " , linkedException );
			throw driverError;
		} else if ( ex instanceof InvocationTargetException ) {
			InvocationTargetException ite = ( InvocationTargetException ) ex;
			Throwable cause = ite.getCause();
			if ( cause != null ) {
				//log as debug and let the convert do the rest for the cause
				Configuration.logDebug ( msg , ite );
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
