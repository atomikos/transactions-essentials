package com.atomikos.recovery;


 /**
  * Non-default OLTP logging can be enabled by registering an instance
  * of this interface via the ServiceLoader mechanism of the JDK.
  * At most one instance is allowed. If none is found, then default logging will be used.
  */

public interface OltpLogFactory {

	/**
	 * @param properties The init properties picked up .
	 */
	public OltpLog createOltpLog();
	
}
