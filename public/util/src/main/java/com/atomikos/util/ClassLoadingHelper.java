/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

/**
 *
 */
package com.atomikos.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * A helper class for class loading.
 * 
 * 
 */

public class ClassLoadingHelper {
	

	private static Set<String> javaLangObjectMethodNames = new HashSet<>();
	static {
		try {
			BeanInfo infos = Introspector.getBeanInfo(java.lang.Object.class);
			MethodDescriptor[] methods=	infos.getMethodDescriptors();
			for (MethodDescriptor methodDescriptor : methods) {
				javaLangObjectMethodNames.add(methodDescriptor.getName());
			}
		} catch (IntrospectionException e) {
			//ignore, return false
		}
	}
	
	public static boolean existsInJavaObjectClass(Method method) {
		return javaLangObjectMethodNames.contains(method.getName());
	}
	private static ClassLoader lastGoodClassLoader;

	/**
	 * Creates a new dynamic proxy instance for the given delegate.
	 * 
	 * @param classLoadersToTry
	 *            The class loaders to try, in the specified list order.
	 * @param interfaces
	 *            The interfaces to add to the returned proxy.
	 * @param delegate
	 *            The underlying object that will receive the calls on the
	 *            proxy.
	 * @return The proxy.
	 * 
	 * @exception IllegalArgumentException
	 *                If any of the interfaces involved could not be loaded.
	 */

	private static <RequiredInterfaceType> RequiredInterfaceType newProxyInstance(Deque<ClassLoader> classLoadersToTry, Class<?>[] interfaces, InvocationHandler delegate) throws IllegalArgumentException {
		RequiredInterfaceType ret = null;
		
		ClassLoader cl = classLoadersToTry.pop();

		try {
			ret= (RequiredInterfaceType) Proxy.newProxyInstance(cl, interfaces, delegate);
			lastGoodClassLoader = cl;
		} catch (IllegalArgumentException someClassNotFound) {
			if (!classLoadersToTry.isEmpty()) {
				// try with remaining class loaders
				ret = newProxyInstance(classLoadersToTry, interfaces, delegate);
			} else {
				// rethrow to caller
				throw someClassNotFound;
			}
		}
		
		return ret;
	}
	private static boolean fallbackToMinimumSetOfInterfaces = false;
	
	private static <RequiredInterfaceType> RequiredInterfaceType newProxyInstanceStartingWithLastGoodClassLoader(Deque<ClassLoader> classLoadersToTry, Class<?>[] interfaces, InvocationHandler delegate) throws IllegalArgumentException {
		RequiredInterfaceType ret = null;
		if (lastGoodClassLoader != null) { //see case 151842
			try {
				ret = (RequiredInterfaceType) Proxy.newProxyInstance(lastGoodClassLoader, interfaces, delegate);				
			} catch (IllegalArgumentException someClassNotFound) {
				//happens if lastGoodClassLoader is no longer valid
			}	
		}
		if (ret == null) { //try the whole list
			ret = newProxyInstance(classLoadersToTry, interfaces, delegate);
		}
		
		
		return ret;
	}
	/**
	 * Creates a new dynamic proxy instance for the given delegate.
	 * 
	 * @param classLoadersToTry
	 *            The class loaders to try, in the specified list order.
	 * @param requiredInterfaceType
	 *            The minimum interface required, if not all interface
	 *            classes were found.
	 * @param interfaces
	 *            The interfaces to add to the returned proxy.
	 * @param delegate
	 *            The underlying object that will receive the calls on the
	 *            proxy.
	 * @return The proxy.
	 * 
	 * @exception IllegalArgumentException
	 *                If any of the interfaces involved could not be loaded.
	 */

	public static <RequiredInterfaceType> RequiredInterfaceType newProxyInstance(Deque<ClassLoader> classLoadersToTry,
			Class<RequiredInterfaceType> requiredInterfaceType, Class<?>[] interfaces,
			InvocationHandler delegate) throws IllegalArgumentException {
		RequiredInterfaceType ret = null;
		if(fallbackToMinimumSetOfInterfaces) {
			Class<?>[] minimumSetOfInterfaces = {requiredInterfaceType};
			ret = newProxyInstanceStartingWithLastGoodClassLoader(classLoadersToTry, minimumSetOfInterfaces, delegate);
		} else {
			try {
				ret = newProxyInstanceStartingWithLastGoodClassLoader(classLoadersToTry, interfaces, delegate);
			} catch (IllegalArgumentException | IllegalAccessError someClassNotFound) {
				fallbackToMinimumSetOfInterfaces = true;
				Class<?>[] minimumSetOfInterfaces = {requiredInterfaceType};
				ret = newProxyInstanceStartingWithLastGoodClassLoader(classLoadersToTry, minimumSetOfInterfaces, delegate);
			}	
		}
		
		return ret;
	}

	
	/**
	 * Loads a class with the given name.
	 * 
	 * @param className
	 * @return The class object
	 * @throws ClassNotFoundException
	 *             If not found
	 */
	public static <T> Class<T> loadClass(String className)
			throws ClassNotFoundException {
		Class<T> clazz = null;
		try {
			clazz = (Class<T>)Thread.currentThread().getContextClassLoader()
					.loadClass(className);
		} catch (ClassNotFoundException nf) {
			clazz = (Class<T>)Class.forName(className);
		}
		return clazz;
	}

	/**
	 * Attempts to load a given resource from the classpath.
	 * 
	 * @param clazz
	 *            The class to use as reference re classpath.
	 * @param resourceName
	 *            The name of the resource
	 * @return The URL to the resource, or null if not found.
	 */
	public static URL loadResourceFromClasspath(Class<?> clazz, String resourceName) {
		URL ret = null;
		// first try from package scope
		ret = clazz.getResource(resourceName);
		if (ret == null) {
			// not found in package -> try from absolute path
			ret = clazz.getResource("/" + resourceName);
		}
		return ret;
	}

	public static Object newInstance(String className) {
		try {
			Class clazz = ClassLoadingHelper.loadClass(className);
			return clazz.newInstance();
		} catch (ClassNotFoundException e) {
			//don't print stackTrace - cf bug 118228
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
