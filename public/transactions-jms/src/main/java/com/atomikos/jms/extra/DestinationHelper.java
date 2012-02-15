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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import com.atomikos.icatch.system.Configuration;
import com.atomikos.jms.AtomikosJMSException;

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
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Failed to find queue with name: " + destinationName , failed );
		}
		if ( destination == null ) {
			try {
				destination = session.createTopic  ( destinationName );
			} catch ( Exception failed ) {
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Failed to find topic with name: " + destinationName , failed );
			}
		}
		if ( destination == null ) {
			AtomikosJMSException.throwAtomikosJMSException ( "The specified destination could not be found: " + destinationName );
		}
		return destination;
	}
}
