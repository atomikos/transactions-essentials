/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.atomikos.beans.PropertyUtils;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

 /**
  * Abstract reusable logic for easy dynamic proxy creation.
  * 
  * Subclasses can have 'overriding' methods annotated with @Proxied 
  * to implement custom logic for a given interface.
  * 
  * 
  * <strong>
  *     IMPORTANT: subclasses and proxied methods must be PUBLIC in scope for this to work!
  *     In addition, subclasses should override toString with descriptive information so logging is clear.
  * </strong>
  */

public abstract class DynamicProxySupport<RequiredInterfaceType> implements InvocationHandler {
	
	private static final Logger LOGGER = LoggerFactory.createLogger(DynamicProxySupport.class);
	
	public static boolean isProxyInstanceOfClass(Class<?> clazz, Object o) {
		return clazz.isAssignableFrom(Proxy.getInvocationHandler(o).getClass());
	}

	protected boolean closed = false;
	protected final RequiredInterfaceType delegate;
	protected final Map<String, Method> proxiedMethods = new HashMap<String, Method>();
	
	protected DynamicProxySupport(RequiredInterfaceType delegate) {
		this.delegate = delegate;
		fillProxiedMethodsCache();
	}
	
	private  void fillProxiedMethodsCache() {
		
			Class<?> dynamicProxyClass = this.getClass();
			Method[] methods = dynamicProxyClass.getMethods();
			if (methods == null) {
				throw new IllegalStateException(dynamicProxyClass.getSimpleName() +": at least one @Proxied method is expected but none was found.");
			}
			boolean proxiedMethodFound = false;			
			for (Method m : methods) {
				if (m.isAnnotationPresent(Proxied.class)) {
					proxiedMethodFound = true;
					proxiedMethods.put(createSignature(m), m);
					
				}
			}
			if (!proxiedMethodFound) {
				throw new IllegalStateException(dynamicProxyClass.getSimpleName() +": at least one @Proxied method is expected but none was found.");
			}	
		
		
	}

	private String createSignature(Method m) {
		StringBuilder ret = new StringBuilder(32);
		ret.append(m.getName());
		for (Class<?> c : m.getParameterTypes()) {
			ret.append(c.getName());
		}
		return ret.toString();
	}
	
	private String formatCallDetails(Method method, Object... args) {
		StringBuffer ret = new StringBuffer();
		ret.append(method.getName());
		if (args!=null && args.length>0) {
			ret.append("(");
			for (int i = 0; i < args.length; i++) {
				ret.append(args[i]);
				if (i < args.length-1) ret.append(",");
			}
			ret.append(")");
		}
		return ret.toString();
	}

	
	private static List<String> methodsAllowedAfterClose = Arrays.asList("close", "isClosed");

	
	private boolean methodAllowedAfterClose(Method method) {
		return methodsAllowedAfterClose.contains(method.getName()) || ClassLoadingHelper.existsInJavaObjectClass(method);
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object ret = null;
		if( closed && !methodAllowedAfterClose(method) ) {
			throwInvocationAfterClose(method.getName());
			return null;
		}
		
		try {		    
		    Method proxiedMethod = findProxiedMethodFor(method);
		    if (proxiedMethod != null) {
		        ret = callProxiedMethod(proxiedMethod, args);
		    } else {
		        ret = callNativeMethod(method, args);		
		    }	
		} catch (InvocationTargetException e) {
		    Throwable cause = e.getCause();
		    if (cause != null) {
		        // If available: throw the underlying vendor exception to preserve the context.
		        handleInvocationException(cause);
		    } else {
		        // Strangely enough the javadoc says 'cause' can be null: 
		        // https://docs.oracle.com/javase/7/docs/api/java/lang/reflect/InvocationTargetException.html
		        // although that does not seem to make sense because 
		        // InvocationTargetException is intended to wrap 
		        // occurrences of underlying exceptions?
		        // Whatever, let's just handle this exotic case to be sure...
		        handleInvocationException(e); 
		    }
		}   
		if (LOGGER.isTraceEnabled()) {
		    LOGGER.logTrace ( this + ": " + method.getName() + " returning " + ret );
		}
		return ret;
	}
	
	/**
	 * Down-call to handle exceptions after an invocation. 
	 * Implementations should decide on whether to mark the proxy as erroneous and 
	 * whether or not to re-throw.
	 * 
	 * @param e
	 * @throws Throwable
	 */
	protected abstract void handleInvocationException(Throwable e) throws Throwable; 

	protected abstract void throwInvocationAfterClose(String method) throws Exception;
	

	private Object callProxiedMethod(Method proxiedMethod, Object... args)
			throws IllegalAccessException, InvocationTargetException {
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.logDebug(this + ": calling proxied " + formatCallDetails(proxiedMethod, args));
		}
		return proxiedMethod.invoke(this, args);
	}


	/**
	 * Delegates the call to the native method in the delegate. This method can safely be reused in subclasses when needed.
	 * 
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	protected Object callNativeMethod(Method method, Object... args) throws Throwable {
		if (LOGGER.isTraceEnabled())  {
		    LOGGER.logTrace(this + ": calling native " + formatCallDetails(method, args));		
		}
		return method.invoke(delegate, args);		
	}

	private Method findProxiedMethodFor(Method method) {
		
		return proxiedMethods.get(createSignature(method));
	}
	
	
	public RequiredInterfaceType createDynamicProxy() {
		
		return ClassLoadingHelper.newProxyInstance(getClassLoadersToTry(), getRequiredInterfaceType(), getInterfaceClasses(), this);
	}
	
	protected Deque<ClassLoader> getClassLoadersToTry() {
		Deque<ClassLoader> classLoaders = new ArrayDeque<ClassLoader>();
		addIfNotNull(classLoaders, Thread.currentThread().getContextClassLoader());
		addIfNotNull(classLoaders, delegate.getClass().getClassLoader());
		addIfNotNull(classLoaders, DynamicProxySupport.class.getClassLoader() );
		return classLoaders;

	}

	protected void addIfNotNull(Deque<ClassLoader> classLoaders, ClassLoader cl) {
		if (cl != null) { //cf case 182578 
			classLoaders.add ( cl );
		}
	}

	protected abstract Class<RequiredInterfaceType> getRequiredInterfaceType();
	
	
	public void markClosed()  {
	    LOGGER.logTrace(this +": marking connection proxy as closed...");
		this.closed = true;
	}
	
	protected Class<?>[] getInterfaceClasses() {
		
		Set<Class<?>> interfaces = PropertyUtils.getAllImplementedInterfaces(delegate.getClass());
		return interfaces.toArray(new Class[0]);
	}
	
}
