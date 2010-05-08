package com.atomikos.jms;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

public class TestExceptionListener implements ExceptionListener 
{

	private boolean notified;
	
	public void onException ( JMSException e ) 
	{
		notified = true;
	}
	
	public boolean wasNotified()
	{
		return notified;
	}

}
