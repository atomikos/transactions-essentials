/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.util;

import junit.framework.TestCase;

public class UniqueIdMgrTestJUnit extends TestCase {

	private UniqueIdMgr idmgr;
	
	protected void setUp() throws Exception {
		super.setUp();
		idmgr = new UniqueIdMgr ( "./testserver" );
	}
	
	
	public void testGetReturnsUniqueId() {
		assertFalse(idmgr.get().equals(idmgr.get()));
	}

}
