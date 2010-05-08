/**
 * 
 */
package com.atomikos.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.List;

/**
 * A helper class for class loading.
 * 
 * 
 */

public class ClassLoadingHelper 
{
	
	/**
	 * Creates a new dynamic proxy instance for the given delegate.
	 * 
	 * @param initialClassLoaders The initial class loaders to try.
	 * @param interfaces The interfaces to add to the returned proxy.
	 * @param delegate The underlying object that will receive the calls on the proxy.
	 * @return The proxy.
	 * 
	 * @exception IllegalArgumentException If any of the interfaces involved could 
	 * not be loaded.
	 */
	
	public static Object newProxyInstance ( final List initialClassLoaders ,
			Class[] interfaces , InvocationHandler delegate )
	throws IllegalArgumentException
	{
		//PLQ not sure It is required
//		for ( int i = 0 ; i < interfaces.length ; i++ ) {
//			Class c = ( Class ) interfaces[i];
//			initialClassLoaders.add ( c.getClassLoader() );
//		}

		final Object[] loaders = initialClassLoaders.toArray();
		
		// cf case 60220: use a class loader that can see ALL classes
		// including those from other OSGi modules
		final ClassLoader l = new ClassLoader() {
			
			
			
			public Class findClass ( String name ) throws ClassNotFoundException 
			{
				
				
				
				Class ret = null;
				if(ret==null){
				int i = 0;
				while ( ret == null && i < loaders.length ) {
					try {
						ClassLoader loader = ( ClassLoader ) loaders[i];
						
						ret = loader.loadClass ( name );
					} catch ( ClassNotFoundException notFound ) {
						// ignore: try with the next loader
					}
					i++;
				}
				}
				if ( ret == null ) throw new IllegalArgumentException ( "Class not found: " + name );

				return ret;
			}
			
			
			public URL getResource ( String name ) {

				URL ret = null;				
				int i = 0;
				while ( ret == null && i < loaders.length ) {
					ClassLoader loader = ( ClassLoader ) loaders[i];
					ret = loader.getResource ( name );
					i++;
				}
				return ret;
			}
		};

		initialClassLoaders.add(l);
		
		return internalNewProxyInstance(initialClassLoaders,interfaces,delegate);
	}
	private static Object internalNewProxyInstance ( 
			List classLoadersToTry , Class[] interfaces , InvocationHandler delegate ) 
		 	throws IllegalArgumentException
	{
		Object ret = null;
		ClassLoader cl = ( ClassLoader ) classLoadersToTry.get ( 0 );
		List remainingClassLoaders = classLoadersToTry.subList ( 1, classLoadersToTry.size() );
		
		try {
			return Proxy.newProxyInstance ( cl , interfaces , delegate );
		} catch ( IllegalArgumentException someClassNotFound ) {
			if ( remainingClassLoaders.size() > 0 ) {
				//try with remaining class loaders
				ret = newProxyInstance ( remainingClassLoaders , interfaces , delegate );
			} else {
				//rethrow to caller
				throw someClassNotFound;
			}
		}
		
		return ret;
	}

	/**
	 * Loads a class with the given name.
	 * 
	 * @param className
	 * @return The class object
	 * @throws ClassNotFoundException If not found
	 */
	public static Class loadClass ( String className ) throws ClassNotFoundException
	{
		Class clazz = null;
		try {
			clazz = Thread.currentThread().getContextClassLoader().loadClass( className );
		} catch ( ClassNotFoundException nf ) {
			clazz = Class.forName ( className );
		}
		return clazz;
	}
}
