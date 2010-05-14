package com.atomikos.util;

import junit.framework.TestCase;

public class UniqueIdMgrTestJUnit extends TestCase {

	private UniqueIdMgr idmgr;
	
	protected void setUp() throws Exception {
		super.setUp();
		idmgr = new UniqueIdMgr ( "./target/testserver" );
	}
	
	public void testNoDuplicatesAcrossEpochs()
	{
		idmgr.epoch_ = 1;
		idmgr.lastcounter_ = 10;
		String id = idmgr.get();
		idmgr.epoch_ = 11;
		idmgr.lastcounter_ = 0;
		if ( id.equals ( idmgr.get() ) ) 
			fail ( "Duplicate id");
	}

}
