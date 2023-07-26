package com.atomikos.recovery.fs;

import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.recovery.LogException;

public interface LogLock {

    void init(ConfigProperties configProperties);

    void acquireLock() throws LogException;

    void releaseLock();
}
