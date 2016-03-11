/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.util;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

/**
 *
 * An intra-VM object registry for reconstructing Objects from references.
 *
 *
 * @author lorban
 */
public class IntraVmObjectRegistry {

	private final static Map<String,Object> resourcesMap = new HashMap<String,Object>();

	public synchronized static void addResource(String resourceName, Object resource) throws NameAlreadyBoundException
	{
		if (resourcesMap.containsKey(resourceName))
			throw new NameAlreadyBoundException("resource with name '" + resourceName + "' already registered");

		resourcesMap.put(resourceName, resource);
	}

	public synchronized static Object getResource(String resourceName) throws NameNotFoundException
	{
		if (!resourcesMap.containsKey(resourceName))
			throw new NameNotFoundException("no resource with name '" + resourceName + "' has been registered yet");

		return resourcesMap.get(resourceName);
	}

	public synchronized static void removeResource(String resourceName) throws NameNotFoundException
	{
		if (!resourcesMap.containsKey(resourceName))
			throw new NameNotFoundException("no resource with name '" + resourceName + "' has been registered yet");

		resourcesMap.remove(resourceName);
	}

}
