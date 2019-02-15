/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Queue;

/**
 * 
 * 
 * 
 * 
 *
 * 
 */
public class TestQueue implements Queue
{

	private String name = "TESTQUEUE";
    
    public String getQueueName() throws JMSException
    {
        return name;
    }
    
    public String toString()
    {
    	return name;
    }

}
