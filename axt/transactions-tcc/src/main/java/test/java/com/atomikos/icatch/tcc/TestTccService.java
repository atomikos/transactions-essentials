package com.atomikos.icatch.tcc;

import java.util.HashMap;
import java.util.Map;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.tcc.TccException;
import com.atomikos.tcc.TccService;

public class TestTccService implements TccService {

	private Map confirmedIds, canceledIds, 
		recoveredIds;
	
	private boolean failOnConfirm, 
		failOnCancel;
	
	public TestTccService()
	{
		reset();
	}
	
	public void reset()
	{
		confirmedIds = new HashMap();
		canceledIds = new HashMap();
		recoveredIds = new HashMap();
		failOnConfirm = false;
		failOnCancel = false;
	}
	
	public void confirm (
			String id ) 
	throws HeurRollbackException, TccException 
	{
		confirmedIds.put ( id , id );
		
		if ( failOnConfirm )
			throw new TccException ( );
	}

	public void cancel ( 
			String id) 
	throws HeurCommitException, TccException {
		//System.out.println ( "service cancel: " + id );
		canceledIds.put ( id , id );
		if ( failOnCancel )
			throw new TccException ( );
	}

	public boolean recover (
			String id ) {
		//System.out.println ( "Recovering id: " + id + " in service " + this );
		recoveredIds.put ( id , id );
		return true;
	}
	
	public boolean isRecovered ( String id )
	{
		return recoveredIds.containsKey ( id );
	}
	
	public boolean isConfirmed ( String id )
	{
		
		return confirmedIds.containsKey ( id );
		
	}
	
	public boolean isCanceled ( String id )
	{
		
		return canceledIds.containsKey ( id );
	}

	public void setFailOnCancel() {
		this.failOnCancel = true;
	}

	

	public void setFailOnConfirm() {
		this.failOnConfirm = true;
	}

}
