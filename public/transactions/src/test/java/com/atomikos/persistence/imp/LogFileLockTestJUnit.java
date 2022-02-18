/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.persistence.imp;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atomikos.recovery.LogException;


public class LogFileLockTestJUnit {

	private LogFileLock lock;
	
	@Before
	public void setUp() throws Exception {
		lock = new LogFileLock("." + File.separatorChar, "LogFileLockTest");
	}
	
	@After
	public void tearDown() throws Exception {
		if (lock != null) lock.releaseLock();
	}

	@Test
	public void testLockWorksForFirstAcquisition() throws LogException {
		lock.acquireLock();
	}
	
	@Test(expected=LogException.class)
	public void testLockFailsForSecondAcquisition() throws LogException {
		lock.acquireLock();
		lock.acquireLock();
	}

	@Test
	public void testLockWorksAfterAcquisitionAndRelease() throws LogException {
		lock.acquireLock();
		lock.releaseLock();
		lock.acquireLock();
	}
	
}
