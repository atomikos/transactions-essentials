/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
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
	
	private final static Map resourcesMap = new HashMap();
	
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
