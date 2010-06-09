package com.atomikos.icatch.imp;

import java.util.Properties;
import java.util.Stack;

import com.atomikos.icatch.TestRecoveryCoordinator;

import junit.framework.TestCase;

public class CompositeTransactionAdaptorTestJUnit extends TestCase {

	private static final String ROOT = "RootId";
	private static final String TEST_PROPERTY_NAME ="name";
	private static final String TEST_PROPERTY_VALUE = "value";
	
	
	private CompositeTransactionAdaptor adaptor;
	private Properties properties;
	private TestRecoveryCoordinator rc;
	
	
	protected void setUp() throws Exception {
		super.setUp();
		properties = new Properties ();
		rc = new TestRecoveryCoordinator();
		properties.setProperty ( TEST_PROPERTY_NAME , TEST_PROPERTY_VALUE );
		adaptor = new CompositeTransactionAdaptor ( 
				ROOT , false , 
				rc , 
				properties	
		);
	}

	public void testRoot()
	{
		assertEquals ( ROOT , adaptor.getCoordinatorId() );
	}
	
	public void testRecoveryCoordinator()
	{
		assertEquals ( rc , adaptor.getRecoveryCoordinator() );
	}
	
	public void testCompositeCoordinator()
	{
		assertNotNull ( adaptor.getCompositeCoordinator() );
		assertEquals ( adaptor , adaptor.getCompositeCoordinator());
	}
	
	public void testRecoverableWhileActive()
	{
		assertNull ( adaptor.isRecoverableWhileActive() );
		try {
			adaptor.setRecoverableWhileActive();
			fail ( "setRecoverableWhileActive works and should not" );
		}
		catch ( UnsupportedOperationException ok ){}
		
		Stack parents = new Stack();
		parents.push( adaptor );
		adaptor = new CompositeTransactionAdaptor ( parents , ROOT , false , rc , new Boolean ( true ) );
		assertTrue ( adaptor.isRecoverableWhileActive().booleanValue() );
		adaptor = new CompositeTransactionAdaptor ( parents , ROOT , false , rc , new Boolean ( false ) );
		assertFalse ( adaptor.isRecoverableWhileActive().booleanValue() );
	}
	
	public void testTags()
	{
		assertNull ( adaptor.getTags() );
	}
}
	
