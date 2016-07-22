/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

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
	
	public int getReceiveTimeout();
	
}
