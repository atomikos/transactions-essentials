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
	@SuppressWarnings("serial")
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
