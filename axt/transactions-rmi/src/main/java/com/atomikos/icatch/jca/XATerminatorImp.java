//$Id: XATerminatorImp.java,v 1.1.1.1 2006/10/02 15:21:16 guy Exp $
//$Log: XATerminatorImp.java,v $
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
//Revision 1.1.1.1  2006/03/09 14:59:31  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/05/16 15:06:29  guy
//Updated tasks.
//
//Revision 1.2  2005/05/09 08:07:03  guy
//Finished JCA package implementation.
//
//Revision 1.1  2005/05/06 11:41:50  guy
//Completed package for JCA inbound transaction handling.
//
package com.atomikos.icatch.jca;

import java.util.HashMap;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.imp.BaseTransactionManager;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * A utility class to terminate XA transactions that
 * were imported.
 * 
 */

public class XATerminatorImp 
{
	
	private static XATerminatorImp singleton;
	
	static synchronized XATerminatorImp
		getInstance()
	{
		if ( singleton == null )
			singleton = new XATerminatorImp();	
		return singleton;
	}

	private HashMap xidToParticipantMap;
	
	private XATerminatorImp() {
		xidToParticipantMap = new HashMap();
	}
	
	
	private synchronized void addToMap ( Xid xid , Participant participant )
	{
		xidToParticipantMap.put ( xid , participant );
	}
	
	private synchronized void removeFromMap ( Xid xid )
	{
		xidToParticipantMap.remove ( xid );
	}
	
	private Participant findInMap ( Xid xid )
	{
		return ( Participant ) xidToParticipantMap.get ( xid );
	}

	
	void registerParticipant ( Xid xid , Participant participant )
	{
		addToMap ( xid , participant );	
	}


	/**
	 * @see javax.resource.spi.XATerminator#commit(javax.transaction.xa.Xid, boolean)
	 */
	public void commit(Xid xid, boolean onePhase) throws XAException
	{
		Participant p = findInMap ( xid );
		if ( p == null )
			throw new XAException ( XAException.XAER_NOTA );
        
        try
        {
            p.commit ( onePhase );
            removeFromMap ( xid );
        }
        catch (SysException e)
        {
            throw e;
        }
        catch (HeurRollbackException e)
        {
            throw new XAException ( XAException.XA_HEURRB );
        }
        catch (HeurHazardException e)
        {
			throw new XAException ( XAException.XA_HEURHAZ ); 
        }
        catch (HeurMixedException e)
        {
			throw new XAException ( XAException.XA_HEURMIX );
        }
        catch (RollbackException e)
        {
			throw new XAException ( XAException.XA_RBROLLBACK );
        }
	}

	/**
	 * @see javax.resource.spi.XATerminator#forget(javax.transaction.xa.Xid)
	 */
	public void forget(Xid xid) throws XAException
	{
		Participant p = findInMap ( xid );
		if ( p == null )
			throw new XAException ( XAException.XAER_NOTA );
			
		p.forget();
		removeFromMap ( xid );
	}

	/**
	 * @see javax.resource.spi.XATerminator#prepare(javax.transaction.xa.Xid)
	 */
	public int prepare(Xid xid) throws XAException
	{
		int ret = Participant.READ_ONLY + 1;
		Participant p = findInMap ( xid );
		if ( p == null )
				throw new XAException ( XAException.XAER_NOTA );
		
		try
        {
        	
            ret = p.prepare();
            if ( ret == Participant.READ_ONLY ) {
           		removeFromMap ( xid );
            	ret = XAResource.XA_RDONLY;
            }
            else ret = XAResource.XA_OK;
        }
        catch (SysException e)
        {
            throw e;
        }
        catch (RollbackException e)
        {
			throw new XAException ( XAException.XA_RBROLLBACK );
        }
        catch (HeurHazardException e)
        {
			throw new XAException ( XAException.XA_HEURHAZ );
        }
        catch (HeurMixedException e)
        {
			throw new XAException ( XAException.XA_HEURMIX );
        }
		
		return ret;
	}

	/**
	 * @see javax.resource.spi.XATerminator#recover(int)
	 */
	public Xid[] recover(int flags) throws XAException
	{
		
		Xid[] ret = new Xid[0];
		boolean newScan = ( flags & XAResource.TMSTARTRSCAN ) == XAResource.TMSTARTRSCAN;
		
		//if new scan is started -> return all XIDs
		if ( newScan ) {
			ret = XidLogAdministrator.getInstance().recover();
			//IMPORTANT: reconstruct mappings for recovered XIDs
			BaseTransactionManager btm = ( BaseTransactionManager ) Configuration.getCompositeTransactionManager();
			for ( int i = 0 ; i < ret.length ; i++ ) {
				String rootTid = XidTransaction.convertXidToTid ( ret[i] );
				Participant p = btm.getParticipant ( rootTid );
				addToMap ( ret[i] , p );
			}
			
		} 
		//if newScan is false: this means repeated calls in same scan -> return empty list
		
		
		return ret;
	}

	/**
	 * @see javax.resource.spi.XATerminator#rollback(javax.transaction.xa.Xid)
	 */
	public void rollback(Xid xid) throws XAException
	{
		Participant p = findInMap ( xid );
		if ( p == null )
			throw new XAException ( XAException.XAER_NOTA );
		
		try
        {
            p.rollback();
			removeFromMap ( xid );
        }
        catch (SysException e)
        {
            throw e;
        }
        catch (HeurCommitException e)
        {
			throw new XAException ( XAException.XA_HEURCOM );
        }
        catch (HeurMixedException e)
        {
			throw new XAException ( XAException.XA_HEURMIX );
        }
        catch (HeurHazardException e)
        {
			throw new XAException ( XAException.XA_HEURHAZ );
        }
        
	}	

}
