package com.atomikos.jms.extra;

 /**
  * Configuration properties for the consumer session.
  * 
  * An interface like this allows for the detection of hot-changes in settings.
  *
  */

interface MessageConsumerSessionProperties 
{
	
	public int getTransactionTimeout();
	
	public boolean getUnsubscribeOnClose();
	
}
