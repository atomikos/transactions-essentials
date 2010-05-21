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
