/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.extra;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import com.atomikos.jms.internal.AtomikosJMSException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

 /**
  * Helper class for common destination logic.
  * 
  *
  */

class DestinationHelper 
{
	private static final Logger LOGGER = LoggerFactory.createLogger(DestinationHelper.class);
	
	/**
	 * Finds a destination with a given provider-specific name.
	 * 
	 * @param destinationName
	 * @param session
	 * @return The destination 
	 * @throws JMSException If not found or if any other JMS error occurs.
	 */
	public static Destination findDestination ( String destinationName , Session session )
	throws JMSException {
		Destination destination = null;
		
		try {
			destination = session.createQueue  ( destinationName );
		} catch ( Exception failed ) {
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "Failed to find queue with name: " + destinationName , failed );
		}
		if ( destination == null ) {
			try {
				destination = session.createTopic  ( destinationName );
			} catch ( Exception failed ) {
				if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "Failed to find topic with name: " + destinationName , failed );
			}
		}
		if ( destination == null ) {
			AtomikosJMSException.throwAtomikosJMSException ( "The specified destination could not be found: " + destinationName );
		}
		return destination;
	}
}
