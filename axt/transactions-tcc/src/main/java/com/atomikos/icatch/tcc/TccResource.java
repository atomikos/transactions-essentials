package com.atomikos.icatch.tcc;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.datasource.ResourceException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryService;

/**
 * 
 * A simple resource, needed to trigger recovery of 
 * all TccParticipant instances once the TccService is in place.
 * In other words, an instance of this class must be added
 * to the TM configuration once the TccService has been registered.
 * 
 *
 */

class TccResource implements RecoverableResource 
{

	
	private String name;
	
	private boolean closed;
	
	TccResource ( String name )
	{
		this.name = name;
		closed = false;
	}

	public void setRecoveryService ( RecoveryService recoveryService )
	throws ResourceException 
	{
		//null during testing
		if ( recoveryService != null ) {
            closed = false;
            recoveryService.recover ();
        }

	}

	public boolean recover ( Participant participant ) 
	throws ResourceException 
	{
		//should never be called -> return false
		return false;
	}

	public void endRecovery() throws ResourceException 
	{
		closed = false;

	}

	public void close() throws ResourceException 
	{
		closed = true;
	}

	public String getName() 
	{
		return name;
	}

	public boolean isSameRM ( RecoverableResource res ) 
	throws ResourceException 
	{
		return res.getName().equals ( name );
	}

	public boolean isClosed() 
	{
		return closed;
	}

}
