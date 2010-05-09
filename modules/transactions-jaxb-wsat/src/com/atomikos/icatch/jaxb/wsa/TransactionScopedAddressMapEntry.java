//$Id: TransactionScopedAddressMapEntry.java,v 1.1.1.1 2006/10/02 15:20:58 guy Exp $
//$Log: TransactionScopedAddressMapEntry.java,v $
//Revision 1.1.1.1  2006/10/02 15:20:58  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:30  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:58  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:24  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/11/14 13:40:26  guy
//Changed auto-removal: only done for ABORTS (otherwise compensation
//will not work: original entry will disappear at early commit!)
//
//Revision 1.3  2005/11/07 10:57:17  guy
//Adapted after testing.
//
//Revision 1.2  2005/10/24 13:28:12  guy
//Improved map manipulation code.
//
//Revision 1.1  2005/10/24 09:50:25  guy
//Added this class to track registration addresses per tx.
//
package com.atomikos.icatch.jaxb.wsa;

import java.util.Map;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.jaxb.wsa.v200408.OutgoingAddressingHeaders;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * A utility class to register addresses for the duration
 * of a transaction. 
 *
 * 
 */
public class TransactionScopedAddressMapEntry implements Synchronization
{
	
	
	/**
	 * Extracts/removes from the given map
	 * @param tid
	 * @param tidToAddressMap
	 * @return The address, or null if not found.
	 */
	public static OutgoingAddressingHeaders extractAddressFromMap ( String tid , Map tidToAddressMap )
	{
		OutgoingAddressingHeaders ret = (OutgoingAddressingHeaders) tidToAddressMap.remove ( tid );
		if ( ret == null ) {
			Configuration.logDebug ( "TransactionScopedAddressMapEntry: no entry found for tid " + tid );
		}
		return ret;
		
	}
	
	private Map myTidToAddressMap;
	private String tid;
   
   /**
    * Creates and registers in the map and with the tx.
    * @param ct The transaction whose completion terminates this entry
    * @param address The address to register in the map, based on the TID
    * @param tidToAddressMap The map to register/remove in
    */
    public TransactionScopedAddressMapEntry ( CompositeTransaction ct , OutgoingAddressingHeaders address , Map tidToAddressMap )
    {
        super();
        if ( ct == null ) throw new IllegalArgumentException ( "Null transaction not allowed");
        if ( address == null ) throw new IllegalArgumentException ( "Null address not allowed");
        this.tid = ct.getTid();
        myTidToAddressMap = tidToAddressMap;
        synchronized ( tidToAddressMap ) {
			tidToAddressMap.put ( tid , address );
			Configuration.logDebug ( "TransactionScopedAddressMapEntry: added entry for tid:  " + tid + " with value " + address );
		
        }
        
        
        ct.registerSynchronization ( this );
    }

    /**
     * @see com.atomikos.icatch.Synchronization#beforeCompletion()
     */
    public void beforeCompletion()
    {
        

    }

    /**
     * @see com.atomikos.icatch.Synchronization#afterCompletion(java.lang.Object)
     */
    public void afterCompletion(Object txstate)
    {
    	//only do this on ABORTING: if commit, then the removal should be done 
    	//during registration in the Importing TM!!!
    	if ( txstate.equals ( TxState.ABORTING ) ) {
    	
        	synchronized ( myTidToAddressMap ) {
        		myTidToAddressMap.remove ( tid );
        		Configuration.logDebug ( "TransactionScopedAddressMapEntry: removed entry for tid:  " + tid );
        	}
    	}

    }

}
