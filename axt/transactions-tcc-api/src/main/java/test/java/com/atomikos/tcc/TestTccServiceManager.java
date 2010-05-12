package com.atomikos.tcc;

import com.atomikos.tcc.TccServiceManager;

public class TestTccServiceManager implements TccServiceManager 
{

	private String lastId;
	
	public String register ( TccService service, long timeout ) 
	{
		lastId =  "" + System.currentTimeMillis();
		return lastId;
	}
	
	public String getLastId()
	{
		return lastId;
	}

	public void completed ( String id ) 
	{
		
	}

	public void failed ( String id ) 
	{


	}

	public void suspend ( String id ) 
	{


	}

	public void resume ( String id ) 
	{


	}

	public void registerForRecovery(TccService service) {
		
		
	}

	public void deregisterForRecovery(TccService service) {
		
		
	}

}
