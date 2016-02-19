/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.persistence.imp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atomikos.recovery.LogException;


public class LogFileLockTestJUnit {

	private LogFileLock lock;
	
	@Before
	public void setUp() throws Exception {
		lock = new LogFileLock("./", "LogFileLockTest");
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
