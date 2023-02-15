/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
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
				parent.mkdirs();
			}
			lockfileToPreventDoubleStartup_ = new File(dir, fileName + ".lck");
			lockfilestream_ = new FileOutputStream(lockfileToPreventDoubleStartup_);
			FileLock tryLock = lockfilestream_.getChannel().tryLock();
			if (tryLock != null) {
				lock_ = tryLock;
				lockfileToPreventDoubleStartup_.deleteOnExit();
				return;
			}
		} catch (OverlappingFileLockException | IOException failedToGetLock) {
			// either may happen on windows
		}
		String msg = "The specified log seems to be in use already: " + fileName + " in "+ dir+". Make sure that no other instance is running, or kill any pending process if needed.";
		LOGGER.logFatal(msg);
		throw new LogException(msg);
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
