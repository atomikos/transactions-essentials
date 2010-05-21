package com.atomikos.jms.extra;

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
			Configuration.logDebug ( "Failed to find queue with name: " + destinationName , failed );
		}
		if ( destination == null ) {
			try {
				destination = session.createTopic  ( destinationName );
			} catch ( Exception failed ) {
				Configuration.logDebug ( "Failed to find topic with name: " + destinationName , failed );
			}
		}
		if ( destination == null ) {
			AtomikosJMSException.throwAtomikosJMSException ( "The specified destination could not be found: " + destinationName );
		}
		return destination;
	}
}
