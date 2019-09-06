/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.util;

import java.util.List;

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
	
	public void testFindAllResourcesOfGivenType() throws Exception {
	    IntraVmObjectRegistry.addResource(name, object);
	    IntraVmObjectRegistry.addResource("bla", new String());
	    List<Object> it = IntraVmObjectRegistry.findAllResourcesOfType(String.class);
	    assertEquals(1, it.size());
	}
}
