/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
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
import java.util.ArrayList;
import java.util.List;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * A helper class for class loading.
 * 
 * 
 */

public class ClassLoadingHelper {
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LoggerFactory
			.createLogger(ClassLoadingHelper.class);

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

	private static Object newProxyInstance(List<ClassLoader> classLoadersToTry,
			Class<?>[] interfaces, InvocationHandler delegate)
			throws IllegalArgumentException {

		Object ret = null;
		ClassLoader cl = classLoadersToTry.get(0);
		List<ClassLoader> remainingClassLoaders = classLoadersToTry.subList(1,
				classLoadersToTry.size());

		try {
			return Proxy.newProxyInstance(cl, interfaces, delegate);
		} catch (IllegalArgumentException someClassNotFound) {
			if (remainingClassLoaders.size() > 0) {
				// try with remaining class loaders
				ret = newProxyInstance(remainingClassLoaders, interfaces,
						delegate);
			} else {
				// rethrow to caller
				throw someClassNotFound;
			}
		}

		return ret;
	}

	/**
	 * Creates a new dynamic proxy instance for the given delegate.
	 * 
	 * @param classLoadersToTry
	 *            The class loaders to try, in the specified list order.
	 * @param minimumSetOfInterfaces
	 *            The minimum set of interfaces required, if not all interface
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

	public static Object newProxyInstance(List<ClassLoader> classLoadersToTry,
			Class<?>[] minimumSetOfInterfaces, Class<?>[] interfaces,
			InvocationHandler delegate) throws IllegalArgumentException {
		Object ret = null;
		try {
			ret = newProxyInstance(classLoadersToTry, interfaces, delegate);
		} catch (IllegalArgumentException someClassNotFound) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.logTrace("could not create Atomikos proxy with all requested interfaces - trying again with minimum set of interfaces");
			}

			ret = newProxyInstance(classLoadersToTry, minimumSetOfInterfaces,
					delegate);
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

	private static List<String> javaLangObjectMethodNames = new ArrayList<String>();
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

}
