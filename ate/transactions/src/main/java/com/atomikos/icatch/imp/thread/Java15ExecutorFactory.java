package com.atomikos.icatch.imp.thread;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.atomikos.icatch.system.Configuration;
import com.atomikos.util.ClassLoadingHelper;

/**
 * This creator uses the Java 1.5 concurrent package. It should not be
 * used unless Java 1.5 is present. The 1.4 backport extends this class, 
 * and only adds other class names.
 * 
 * @author Lars J. Nilsson
 */
class Java15ExecutorFactory implements ExecutorFactory 
{

	public static final String MAIN_CLASS = "java.util.concurrent.ThreadPoolExecutor";
	public static final String IQUEUE_CLASS = "java.util.concurrent.BlockingQueue";
	public static final String QUEUE_CLASS = "java.util.concurrent.SynchronousQueue";
	public static final String TIMEUNIT_CLASS =  "java.util.concurrent.TimeUnit";
	public static final String IFACTORY_CLASS =  "java.util.concurrent.ThreadFactory";
	
	
	// --- INSTANCE MEMBERS --- //
	
	private Class mainClass;
	private Constructor constructor;
	private Method submit, shutdown;

	
	protected Java15ExecutorFactory() throws Exception 
	{
		checkInit();
	}
	

	// --- REFLECTION CREATOR --- //
	
	public InternalSystemExecutor createExecutor() throws Exception
	{
		Object[] params = toConstructionParameters();
		return new Executor(constructor.newInstance(params), submit, shutdown);
	}
	
	
	// -- PROTECTED METHODS --- //

	/**
	 * Given the arguments, make an object array which corresponds to the underlying constructor.
	 */
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

	/**
	 * Given the main pool class, extract the constructor to use.
	 */
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
	
	
	/**
	 * Load a given class, and make sure the "preferContext" member is
	 * check to use the correct order of class loading.
	 */
	
	protected Class loadClass(String name) throws Exception
	{
		Class cl = null;
		cl = safeLoad(name);
		if(cl == null) {
			cl = safeContextLoad(name);
		}
		if(cl == null)
			throw new ClassNotFoundException("Class '" + name + "' not found");
		return cl;
	}

	/**
	 * Load the main pool class.
	 */
	protected Class loadMainPoolClass() throws Exception
	{
		return loadClass(MAIN_CLASS);
	}
	
	/**
	 * Extract the "submit" method from the main pool class.
	 */
	protected Method extractSubmitMethod(Class poolClass) throws Exception
	{
		return poolClass.getMethod("execute", new Class[] { Runnable.class });
	}
	
	
	/**
	 * Extract the "shutdown" method from the main pool class.
	 */
	protected Method extractShutdownMethod(Class poolClass) throws Exception
	{
		return poolClass.getMethod("shutdown", new Class[0]);
	}
	
	
	// --- PRIVATE METHODS --- //
	
	private Class safeLoad(String name)
	{
		try {
			return ClassLoadingHelper.loadClass(name);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	private Class safeContextLoad(String name)
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			return (loader == null ? null : loader.loadClass(name));
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	private Object getSecondTimeUnit() throws Exception
	{
		Class cl = loadClass(TIMEUNIT_CLASS);
		Class classClass = cl.getClass();
		Method m = classClass.getMethod("getEnumConstants", null);
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
	
	private synchronized void checkInit() throws Exception
	{
		if(mainClass != null) return; // ALREADY INITIALIZED
		mainClass = loadMainPoolClass();
		constructor = extractConstructor(mainClass);
		submit = extractSubmitMethod(mainClass);
		shutdown = extractShutdownMethod(mainClass);
	}
	
	
	
	// --- INNER CLASSES --- //
	
	/*
	 * Reflection based executor
	 */
	private static final class Executor implements InternalSystemExecutor
	{
		private final Method submit;
		private final Method shutdown;
		private final Object target;

		private Executor(Object target, Method submit, Method shutdown) {
			this.target = target;
			this.submit = submit;
			this.shutdown = shutdown;
		}
		
		public void shutdown() {
			try {
				shutdown.invoke(target, new Object[0]);
			} catch (Exception e) {
				Configuration.logWarning("Failed to shutdown 1.5 concurrent thread pool", e);
			} 
		}
		
		public void execute(Runnable task) {
			try {
				Configuration.logDebug("(1.5) executing task: " + task);
				submit.invoke(target, new Object[] { task });
			} catch (Exception e) {
				Configuration.logWarning("Failed to invoke 1.5 concurrent thread pool", e);
			} 
		}
	}
	
	
	/*
	 * Proxy class for pretending to be a java1.5 thread factory
	 */
	protected static final class FactoryProxy implements InvocationHandler
	{
		protected FactoryProxy()
		{
		}
		
		public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			return ThreadFactory.getInstance().newThread((Runnable)args[0]);
		}
	}
	
}
