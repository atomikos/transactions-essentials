package com.atomikos.icatch.jca;

import java.util.Stack;

import com.atomikos.icatch.Propagation;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * 
 * A propagation to mask inbound XA transactions as
 * icatch propagations. This way, Xid instances can
 * be imported in the local VM.
 * 
 */

class XidPropagation implements Propagation
{

	private XidTransaction transaction;
	
	private long timeout;
	
	XidPropagation ( XidTransaction transaction , 
		long timeout )
	{
		this.transaction = transaction;
		this.timeout = timeout;		
	}

    /**
     * @see com.atomikos.icatch.Propagation#getLineage()
     */
    public Stack getLineage()
    {
        return transaction.getLineage();
    }

    /**
     * @see com.atomikos.icatch.Propagation#isSerial()
     */
    public boolean isSerial()
    {
    	return transaction.isSerial();
    }

    /**
     * @see com.atomikos.icatch.Propagation#getTimeOut()
     */
    public long getTimeOut()
    {
        return timeout;
    }
    
    public boolean isActivity()
    {
        return false;
    }

}
