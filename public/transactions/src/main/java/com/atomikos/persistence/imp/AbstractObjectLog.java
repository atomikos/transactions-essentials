package com.atomikos.persistence.imp;

import com.atomikos.persistence.LogException;
import com.atomikos.persistence.ObjectLog;

public abstract class AbstractObjectLog implements ObjectLog {

	public AbstractObjectLog() {
		super();
	}

	public abstract void flush(SystemLogImage img, boolean shouldSync) throws LogException;
}