/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.persistence.imp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.LogException;

public class LogFileLock {
	
	private static Logger LOGGER = LoggerFactory.createLogger(LogFileLock.class);
	private static final String FILE_SEPARATOR = String.valueOf(File.separatorChar);
	private File lockfileToPreventDoubleStartup_;
	private FileOutputStream lockfilestream_ = null;
	private FileLock lock_ = null;

	private String dir;

	private String fileName;

	public LogFileLock(String dir, String fileName) {
		if(!dir.endsWith(FILE_SEPARATOR)) {
			dir += FILE_SEPARATOR;
		}
		this.dir = dir;
		this.fileName = fileName;
	}

	public void acquireLock() throws LogException {
		try {
			File parent = new File(dir);
			if(!parent.exists()) {
				parent.mkdir();
			}
			lockfileToPreventDoubleStartup_ = new File(dir, fileName + ".lck");
			lockfilestream_ = new FileOutputStream(lockfileToPreventDoubleStartup_);
			lock_ = lockfilestream_.getChannel().tryLock();
			lockfileToPreventDoubleStartup_.deleteOnExit();
		} catch (OverlappingFileLockException failedToGetLock) {
			// happens on windows
			lock_ = null;
		} catch (IOException failedToGetLock) {
			// happens on windows
			lock_ = null;
		}
		if (lock_ == null) {
			LOGGER.logWarning("ERROR: the specified log seems to be in use already: " + fileName + " in "+ dir+". Make sure that no other instance is running, or kill any pending process if needed.");
			throw new LogException("Log already in use? " + fileName + " in "+ dir);
		}
	}

	public void releaseLock() {
		try {
			if (lock_ != null) {
				lock_.release();
			}
			if (lockfilestream_ != null)
				lockfilestream_.close();
		} catch (IOException e) {
			LOGGER.logWarning("Error releasing file lock: " + e.getMessage());
		} finally {
			lock_ = null;
		}

		if (lockfileToPreventDoubleStartup_ != null) {
			lockfileToPreventDoubleStartup_.delete();
			lockfileToPreventDoubleStartup_ = null;
		}
	}
	
}
