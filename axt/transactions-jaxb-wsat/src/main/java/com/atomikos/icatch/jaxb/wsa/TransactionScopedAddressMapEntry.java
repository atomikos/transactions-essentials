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
