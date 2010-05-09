//$Id: XidPropagation.java,v 1.1.1.1 2006/10/02 15:21:16 guy Exp $
//$Log: XidPropagation.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:16  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:45  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:38  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:01  guy
//Import.
//
//Revision 1.2  2006/03/21 13:23:58  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:31  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.1  2005/05/04 09:07:39  guy
//Added core package for JCA inbound transaction support.
//
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
