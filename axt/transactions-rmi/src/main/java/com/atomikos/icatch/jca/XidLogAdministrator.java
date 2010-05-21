package com.atomikos.icatch.jca;

import java.util.ArrayList;

import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.icatch.admin.LogAdministrator;
import com.atomikos.icatch.admin.LogControl;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * 
 * A log administrator for performing recovery-related
 * actions in inbound JCA transaction cases.
 * 
 */

public class XidLogAdministrator implements LogAdministrator
{
	private static XidLogAdministrator singleton;
	
	public synchronized static XidLogAdministrator getInstance()
	{
		if ( singleton == null )
			singleton = new XidLogAdministrator();
		return singleton;
	}
	
	private LogControl control;
	
	private XidLogAdministrator() {}
	
	private boolean needsRecovery ( AdminTransaction tx )
	{
		boolean ret = false;
		
		int state = tx.getState();
		switch ( state ) {
			case AdminTransaction.STATE_HEUR_ABORTED:
				 ret = true;
				 break;
			case AdminTransaction.STATE_HEUR_COMMITTED:
				 ret = true;
				 break;
			case AdminTransaction.STATE_HEUR_HAZARD:
				 ret = true;
				 break;
			case AdminTransaction.STATE_HEUR_MIXED:
				 ret = true;
				 break;
			case AdminTransaction.STATE_PREPARED:
				 ret = true;
				 break;
			
			default: 
				ret = false; 
				break;
		}
		
		return ret;
	}

    /**
     * @see com.atomikos.icatch.admin.LogAdministrator#registerLogControl(com.atomikos.icatch.admin.LogControl)
     */
    public void registerLogControl ( LogControl control )
    {
        this.control = control;
        Configuration.logDebug ( "XidLogAdministrator: register control");
		
    }

    /**
     * @see com.atomikos.icatch.admin.LogAdministrator#deregisterLogControl(com.atomikos.icatch.admin.LogControl)
     */
    public void deregisterLogControl(LogControl control)
    {
        this.control = null;

		Configuration.logDebug ( "XidLogAdministrator: deregistering control");
    }
    
    public Xid[] recover()
    throws XAException
    {
    	ArrayList xidList = new ArrayList();
    	
    
    	if ( this.control == null )
    		 throw new XAException ( "Can't recover if transaction service is down" );
    	
    	AdminTransaction[] txs = control.getAdminTransactions();
    	if ( txs != null ) {
    		for ( int i = 0 ; i < txs.length ; i++ ) {
    			AdminTransaction current = txs[i];
    			if ( needsRecovery ( current ) ) {
    				Xid xid = XidTransaction.convertTidToXid ( current.getTid() );
    				xidList.add ( xid );
    			}
    		}
    	}
    	
    	return ( Xid[] ) xidList.toArray ( new Xid[0] );
    }
    
    public void forget ( Xid xid )
    {
    	if ( control == null ) return;
    	
    	String tid = XidTransaction.convertXidToTid ( xid );
    	String[] tids = { tid };
    	AdminTransaction[] txs = control.getAdminTransactions ( tids );
    	if ( txs != null && txs.length == 1 ) {
    		txs[0].forceForget();
    	}
    }

}
