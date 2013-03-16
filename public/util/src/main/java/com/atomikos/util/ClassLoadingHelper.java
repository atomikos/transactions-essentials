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

/**
 *
 */
package com.atomikos.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;
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
	private static final Logger LOGGER = LoggerFactory.createLogger(ClassLoadingHelper.class);

	/**
	 * Creates a new dynamic proxy instance for the given delegate.
	 *
	 * @param classLoadersToTry
	 *            The class loaders to try, in the specified list order.
	 * @param interfaces
	 *            The interfaces to add to the returned proxy.
	 * @param delegate
	 *            The underlying object that will receive the calls on the proxy.
	 * @return The proxy.
	 *
	 * @exception IllegalArgumentException
	 *                If any of the interfaces involved could not be loaded.
	 */

	private static Object newProxyInstance(List classLoadersToTry, Class[] interfaces, InvocationHandler delegate) throws IllegalArgumentException {

		Object ret = null;
		ClassLoader cl = (ClassLoader) classLoadersToTry.get(0);
		List remainingClassLoaders = classLoadersToTry.subList(1, classLoadersToTry.size());

		try {
			return Proxy.newProxyInstance(cl, interfaces, delegate);
		} catch (IllegalArgumentException someClassNotFound) {
			if (remainingClassLoaders.size() > 0) {
				// try with remaining class loaders
				ret = newProxyInstance(remainingClassLoaders, interfaces, delegate);
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
	 *            The minimum set of interfaces required, if not all interface classes were found.
	 * @param interfaces
	 *            The interfaces to add to the returned proxy.
	 * @param delegate
	 *            The underlying object that will receive the calls on the proxy.
	 * @return The proxy.
	 *
	 * @exception IllegalArgumentException
	 *                If any of the interfaces involved could not be loaded.
	 */

	public static Object newProxyInstance(List classLoadersToTry, Class[] minimumSetOfInterfaces, Class[] interfaces, InvocationHandler delegate) throws IllegalArgumentException {
		Object ret = null;
		try {
			ret = newProxyInstance(classLoadersToTry, interfaces, delegate);
		} catch (IllegalArgumentException someClassNotFound) {
			LOGGER.logWarning("could not create Atomikos proxy with all requested interfaces - trying again with minimum set of interfaces");
			ret = newProxyInstance(classLoadersToTry, minimumSetOfInterfaces, delegate);
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
	public static Class loadClass(String className) throws ClassNotFoundException {
		Class clazz = null;
		try {
			clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
		} catch (ClassNotFoundException nf) {
			clazz = Class.forName(className);
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
	public static URL loadResourceFromClasspath(Class clazz, String resourceName) {
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
			// ???
			// Constructor[] contructors= clazz.getDeclaredConstructors();
			// contructors[0].setAccessible(true);

			return clazz.newInstance();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] toByteArray(Serializable obj) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
		serialize(obj, baos);
		return baos.toByteArray();
	}

	private static void serialize(Serializable obj, OutputStream outputStream) throws IOException {
		// stream closed in the finally
		ObjectOutputStream out = new ObjectOutputStream(outputStream);
		out.writeObject(obj);
	}

	public static Object toObject(byte[] bytes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		return deserialize(bais);
	}


	private static Object deserialize(InputStream inputStream) {
		ObjectInputStream in = null;
		try {
		         // stream closed in the finally
	            in = new ObjectInputStream(inputStream);
	            return in.readObject();

		  } catch (ClassNotFoundException ex) {
			  throw new RuntimeException(ex);
		  } catch (IOException ex) {
			  throw new RuntimeException(ex);
		  } finally {
		         try {
		              if (in != null) {
		                in.close();
		              }
		           } catch (IOException ex) {
		               // ignore close exception
		           }
		       }
		  }
}
