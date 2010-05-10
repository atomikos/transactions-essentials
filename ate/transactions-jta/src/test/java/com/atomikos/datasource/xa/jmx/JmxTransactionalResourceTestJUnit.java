package com.atomikos.datasource.xa.jmx;

import com.atomikos.datasource.xa.TestXATransactionalResource;

import junit.framework.TestCase;

public class JmxTransactionalResourceTestJUnit extends TestCase {

	private JmxTransactionalResource resource;
	
	protected void setUp() throws Exception {
		super.setUp();
		resource = new JmxTransactionalResource (
				new TestXATransactionalResource( null , "test") ,
				new XAResourceConfig() ,
				"localName"
		);
	}
	
	public void testUsesWeakCompare() {
		resource.setUseWeakCompare ( true );
		assertTrue ( resource.getUseWeakCompare() );
		resource.setUseWeakCompare ( false );
		assertFalse ( resource.getUseWeakCompare() );
	}
	

	public void testAcceptsAllXAResources() {
		assertFalse ( resource.getAcceptAllXAResources() );
		resource.setAcceptAllXAResources ( true );
		assertTrue ( resource.getAcceptAllXAResources() );
		resource.setAcceptAllXAResources ( false );
		assertFalse ( resource.getAcceptAllXAResources() );
	}
	
	public void testJmxCalls() throws Exception {
		resource.preRegister( null , null );
		resource.postRegister ( null );
		resource.preDeregister();
		resource.postDeregister();
	}
}
