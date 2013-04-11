package com.atomikos.persistence.dataserializable;

import com.atomikos.persistence.LogException;
import com.atomikos.persistence.ObjectLog;

public abstract class AbstractObjectLog implements ObjectLog {

	public AbstractObjectLog() {
		super();
	}

	protected abstract void flush(SystemLogImage img, boolean shouldSync) throws LogException;
}