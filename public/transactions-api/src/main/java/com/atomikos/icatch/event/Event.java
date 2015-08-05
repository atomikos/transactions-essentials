package com.atomikos.icatch.event;

import java.io.Serializable;

public abstract class Event implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public long eventCreationTimestamp;
	
	protected Event() {
		this.eventCreationTimestamp = System.currentTimeMillis();
	}

}
