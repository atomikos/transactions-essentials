package com.atomikos.osgi;

import junit.framework.TestCase;

 /**
  * Integration tests for OSGi.
  * 
  * This test will probably not work from within the IDE, since the 
  * OSGi jars are only created during the ant build.
  *
  */

public class OsgiIntegrationTestJUnit extends TestCase 
{

	private TestOsgiRuntime osgiRuntime;
	
	protected void setUp() throws Exception {
		super.setUp();
		osgiRuntime = new TestOsgiRuntime();
		osgiRuntime.start();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if ( osgiRuntime != null ) {
			osgiRuntime.stop();
		}
	}
	
	public void testAtomikosBundlesCanBeLoadedSuccessfully() {
		osgiRuntime.assertAtomikosBundlesWereLoadedSuccessfully();
	}

}
