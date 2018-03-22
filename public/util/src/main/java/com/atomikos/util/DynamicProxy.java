/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.util;



 /**
  * An interface to improve performance of dynamic proxies.
  * 
  * The added method 'getInvocationHandler' avoids calls to the
  * Proxy class (which suffers from performance issues related to 
  * synchronization overhead).
  *
  */

public interface DynamicProxy 
{
	/**
	 * Gets the underlying object that does the proxying.
	 *
	 * 
	 * @return The object, equivalent to calling Proxy.getInvocationHandler(dynamicProxy).
	 */

	public Object getInvocationHandler();
}
