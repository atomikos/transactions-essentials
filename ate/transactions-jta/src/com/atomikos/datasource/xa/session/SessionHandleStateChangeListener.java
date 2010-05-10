package com.atomikos.datasource.xa.session;

public interface SessionHandleStateChangeListener {
	
	/**
	 * fired when all contexts of the SessionHandleState changed to terminated state
	 */
	void onTerminated();

}
