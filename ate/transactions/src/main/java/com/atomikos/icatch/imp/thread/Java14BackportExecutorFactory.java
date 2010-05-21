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

package com.atomikos.icatch.imp.thread;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.atomikos.util.ClassLoadingHelper;

/**
 * This is the backport thread pool creator.
 * 
 * @author Lars J. Nilsson
 */
public class Java14BackportExecutorFactory 
extends Java15ExecutorFactory  {

	public static final String MAIN_CLASS = "edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor";
	public static final String IQUEUE_CLASS = "edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue";
	public static final String QUEUE_CLASS = "edu.emory.mathcs.backport.java.util.concurrent.SynchronousQueue";
	public static final String TIMEUNIT_CLASS =  "edu.emory.mathcs.backport.java.util.concurrent.TimeUnit";
	public static final String IFACTORY_CLASS =  "edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory";
	
	
	// --- INSTANCE MEMBERS --- //

	protected Java14BackportExecutorFactory() throws Exception
	{ 
	}
	
	protected Constructor extractConstructor(Class poolClass) throws Exception
	{
		return poolClass.getConstructor(new Class[] {
				Integer.TYPE,
				Integer.TYPE,
				Long.TYPE,
				loadClass(TIMEUNIT_CLASS),
				loadClass(IQUEUE_CLASS),
				loadClass(IFACTORY_CLASS),
		});
	}
	
	protected Object[] toConstructionParameters() throws Exception
	{
		ClassLoader loader = getClass().getClassLoader();
		Object factory = Proxy.newProxyInstance(loader, new Class[] { loadClass(IFACTORY_CLASS) }, new FactoryProxy());
		Object queue = loadClass(QUEUE_CLASS).newInstance();
		return new Object[] {
			new Integer(0),
			new Integer(Integer.MAX_VALUE),
			new Long(60L),
			getSecondTimeUnit(),
			queue,
			factory,
		};
	}

	protected Class loadMainPoolClass() throws Exception
	{
		return loadClass(MAIN_CLASS);
	}
	
	
	// --- PRIVATE METHODS --- //
	
	private static Object getSecondTimeUnit() throws Exception
	{
		Class cl = ClassLoadingHelper.loadClass ( TIMEUNIT_CLASS );
		Method m = cl.getMethod("values", null);
		Object o = m.invoke(cl, null);
		int len = Array.getLength(o);
		for(int i = 0; i < len; i++) {
			Object test = Array.get(o, i);
			if(test.toString().equals("SECONDS")) {
				return test;
			}
		}
		return null;
	}
}
