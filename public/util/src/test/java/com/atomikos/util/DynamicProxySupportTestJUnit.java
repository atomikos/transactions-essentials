/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.util;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DynamicProxySupportTestJUnit {
	
	private static Class<?>[] INTERFACES = new Class[] { TestInterface.class};
	private TestInterface dynamicProxy;
	private TestInterface delegate;
	private ProxiedClass proxy;
	
	@Before
	public void setUp() {
		delegate = Mockito.mock(TestInterface.class);
		proxy = new ProxiedClass(delegate);
		List<ClassLoader> classLoadersToTry = new ArrayList<ClassLoader>();
		classLoadersToTry.add(this.getClass().getClassLoader());
		dynamicProxy = (TestInterface)Proxy.newProxyInstance(getClass().getClassLoader(), INTERFACES, proxy);
	}
	

	@Test
	public void testProxiedMethod() {
		dynamicProxy.methodToProxy();
		Mockito.verifyZeroInteractions(delegate);
	}
	
	@Test
	public void testOverloadedProxiedMethod() {
		dynamicProxy.methodToProxy(10);
		Mockito.verify(delegate, Mockito.times(1)).methodToProxy(Mockito.anyInt());
	}
	
	@Test
	public void testNativeMethod() {
		dynamicProxy.nativeMethod();
		Mockito.verify(delegate, Mockito.times(1)).nativeMethod();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testThrowInvocationAfterClose() throws Exception {
		dynamicProxy.close();
		dynamicProxy.methodToProxy();
	}
	
	public static class ProxiedClass extends DynamicProxySupport<TestInterface> {
				
		ProxiedClass(TestInterface delegate) {
			super(delegate);
		}


		@Proxied
		public void methodToProxy() {
			// do NOT delegate - for test purposes
		}
		
		@Proxied
		public void methodToProxy(int parameter) {
			delegate.methodToProxy(parameter);
		}
		
		@Proxied
		public void close() {
			markClosed();
		}
		
		public void nativeMethod() {
			delegate.nativeMethod();
		}

		@Override
		protected void throwInvocationAfterClose(String method) throws Exception {
			throw new IllegalStateException("method "+method+" not allowed after close");
		}


        @Override
        protected void handleInvocationException(Throwable e) throws Throwable {
            throw e;
        }

		
	}
	
	interface TestInterface {
		
		void methodToProxy();
		void nativeMethod();
		void methodToProxy(int parameter);
		void close();
	}

}
