package com.atomikos.jms;

 /**
  * Constant values to indicate / tune the behavior in terms of JMS session creation
  * (i.e., under what conditions to create XA sessions vs local sessions),
  * so you can control backwards compatibility of your configuration.
  * 
  * For historical reasons there have been several different interpretations
  * of the JMS session creation semantics, mainly due to unclear JMS specifications.
  */

public final class SessionCreationMode {
	
	static final void assertValidityOf(int value) {
		if (value < 0 || value > 2) {
			throw new IllegalArgumentException("The specified value should be between 0 and 2");
		}
	}
	
	/**
	 * JMS session creation like in Atomikos releases prior to 3.9.
	 */
	
	public static int PRE_3_9 = 0;
	
	/**
	 * JMS session creation like in Atomikos releases [3.9-6.0).
	 */
	
	public static int PRE_6_0 = 1;
	
	/**
	 * As of Atomikos release 6.0, this is the default: 
	 * JMS session creation along the clarified JMS 2.0 specification guidelines,
	 * see https://jakarta.ee/specifications/messaging/2.0/apidocs/ for details.
	 */
	
	public static int JMS_2_0 = 2;
}