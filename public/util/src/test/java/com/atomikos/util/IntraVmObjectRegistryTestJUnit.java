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

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import junit.framework.TestCase;

public class IntraVmObjectRegistryTestJUnit extends TestCase {

	private Integer object;
	private String name;
	
	
	protected void setUp() throws Exception {
		super.setUp();
		name = getName();
		object = new Integer(1);
	}
	
	public void testGetResourceWithoutPriorAdd() throws Exception
	{
		try {
			IntraVmObjectRegistry.getResource ( name );
			fail ( "get works without prior add?" );
		}
		catch ( NameNotFoundException ok ) {}
	}

	public void testGetResourceWithPriorAdd() throws Exception
	{
		IntraVmObjectRegistry.addResource(name, object );
		Integer res = ( Integer ) IntraVmObjectRegistry.getResource(name);
		if (  res == null ) fail ( "get fails after add?" );
	}
	
	public void testRemoveResourceWithoutPriorAdd() throws Exception
	{
		try {
			IntraVmObjectRegistry.removeResource ( name );
			fail ( "remove works without prior add?" );
		}
		catch ( NameNotFoundException ok ) {}
	}
	
	public void testRemoveResourceWithPriorAdd() throws Exception
	{
		IntraVmObjectRegistry.addResource(name, object );
		IntraVmObjectRegistry.removeResource(name);
		try {
			IntraVmObjectRegistry.getResource ( name );
			fail ( "get works after remove?" );
		} 
		catch ( NameNotFoundException ok  ) {}
	}
	
	public void testAddSecondObjectWithSameName() throws Exception
	{
		IntraVmObjectRegistry.addResource(name, object);
		try {
			IntraVmObjectRegistry.addResource(name, object);
			fail ( "second add with same name works" );
			
		}
		catch ( NameAlreadyBoundException ok ) {}
	}
}
