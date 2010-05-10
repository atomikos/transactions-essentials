package com.atomikos.datasource.xa.session;

import com.atomikos.datasource.xa.session.SessionHandleStateChangeListener;

public class TestSessionHandleState implements
		SessionHandleStateChangeListener {

	private boolean terminated;
	
	public TestSessionHandleState () {
		terminated = false;
	}
	
	public void onTerminated() {
		terminated = true;
	}
	
	public boolean isTerminated() {
		return terminated;
	}

}
