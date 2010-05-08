package com.atomikos.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.naming.Reference;

import junit.framework.TestCase;

public class IntraVmObjectFactoryTestJUnit extends TestCase {

	IntraVmObjectFactory fact;
	
	protected void setUp() throws Exception {
		super.setUp();
		fact = new IntraVmObjectFactory();
	}
	
	public void testReturnsNullForNonReferenceArgument() throws Exception {
		Object ref = new Object();
		assertNull ( fact.getObjectInstance( ref , null , null , null ) );
	}
	
	public void testReturnsNullForNullArgument() throws Exception {
		Object ref = null;
		assertNull ( fact.getObjectInstance( ref , null , null , null ) );
	}
	
	public void testReturnsObjectIfBoundInRegistry() throws Exception {
		TestSerializableLocalResource object = new TestSerializableLocalResource();
		//following removed: creating a reference now also adds in the registry
		//IntraVmObjectRegistry.addResource( object.getUniqueResourceName() , object );
		Reference ref = IntraVmObjectFactory.createReference( object , object.getUniqueResourceName() );
		Object lookup = fact.getObjectInstance( ref , null , null , null );
		assertNotNull ( lookup );
		assertSame ( object , lookup );
	}
	
	public void testReferenceContainsRightObjectFactory() throws Exception 
	{
		//test for bug 23617
		TestSerializableLocalResource object = new TestSerializableLocalResource();
		Reference ref = IntraVmObjectFactory.createReference( object , object.getUniqueResourceName() );
		assertEquals ( IntraVmObjectFactory.class.getName() , ref.getFactoryClassName() );
	}
	
	//relevant for lookup in remote JNDI server
	public void testInitIfNotAlreadyBoundInRegistry() throws Exception {
		TestSerializableLocalResource object = new TestSerializableLocalResource();
		Reference ref = IntraVmObjectFactory.createReference( object , getName() );
		//simulate remote VM by removing the registration created by createReference
		IntraVmObjectRegistry.removeResource( getName() );
		TestSerializableLocalResource result = ( TestSerializableLocalResource ) fact.getObjectInstance( ref , null , null , null );
		//no existing instance registered -> init should have happened
		assertTrue ( result.wasInitCalled() );
	}
	
	static class TestSerializableLocalResource implements Serializable {

		private boolean initCalled;
		private long id;
		
		TestSerializableLocalResource() {
			this.id = System.currentTimeMillis();
			
		}
		
		public String getUniqueResourceName() {
			return "" + id;
		}
		
		private void readObject ( ObjectInputStream in ) throws IOException , ClassNotFoundException 
		{
			in.defaultReadObject();
			initCalled = true;
		}

		
		public boolean wasInitCalled() {
			return initCalled;
		}
		
		public boolean equals ( Object o ) {
			boolean ret = false;
			if ( o instanceof TestSerializableLocalResource ) {
				TestSerializableLocalResource other = ( TestSerializableLocalResource ) o;
				ret = id == other.id;
			}
			return ret;
		}
		
	};

}
