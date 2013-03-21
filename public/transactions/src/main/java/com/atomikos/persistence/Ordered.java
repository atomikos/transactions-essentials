package com.atomikos.persistence;

/**
 * @author pascalleclercq
 * Directly inspired from 
 * @see  <a href="http://static.springsource.org/spring/docs/2.0.x/api/org/springframework/core/Ordered.html">Ordered interface from Spring</a> 
 *  
 */
public interface Ordered {

	int HIGHEST_PRECEDENCE=Integer.MIN_VALUE;
	
	int LOWEST_PRECEDENCE=Integer.MAX_VALUE;
	
	/**
	 * @return an arbitrary value that declare a relative priority of the implementor if multiple are found.
	 */
	int getOrder();
}
