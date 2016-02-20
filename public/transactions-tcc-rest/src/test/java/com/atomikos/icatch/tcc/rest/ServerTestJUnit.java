/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.tcc.rest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerTestJUnit {
	
	private static final String URL = "http://localhost:9009";

	private Server server;
	
	@Before
	public void setUp() throws Exception {
		server = new Server(URL);
	}

	@After
	public void tearDown() throws Exception {
		server.stop(true);
	}

	@Test
	public void testStart() {
		server.start();
	}
	
	@Test
	public void testRestart() {
		server.start();
		server.stop(true);
		server.start();
	}

}
