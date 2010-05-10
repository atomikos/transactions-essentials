package com.atomikos.jms.extra;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import com.atomikos.jms.AtomikosJMSException;

class RetrieveDestinationCallback implements JmsSenderTemplateCallback 
{

	private String destinationName;
	private Destination destination;
	
	RetrieveDestinationCallback ( String destinationName ) {
		this.destinationName = destinationName;
	}
	
	public void doInJmsSession ( Session session ) throws JMSException 
	{
		if ( destinationName == null ) 
			AtomikosJMSException.throwAtomikosJMSException ( 
			"Property 'destinationName' was not set" );

		destination = DestinationHelper.findDestination ( destinationName , session );
	}

	Destination getDestination() 
	{
		return destination;
	}
	
}
