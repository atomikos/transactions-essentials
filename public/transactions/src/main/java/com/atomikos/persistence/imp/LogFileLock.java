/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
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
import java.util.concurrent.TimeUnit;

import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.LogException;
import com.atomikos.thread.InterruptedExceptionHelper;

public class LogFileLock {

	private static Logger LOGGER = LoggerFactory.createLogger(LogFileLock.class);
	private static final String FILE_SEPARATOR = String.valueOf(File.separatorChar);
	private File lockfileToPreventDoubleStartup_;
	private FileOutputStream lockfilestream_ = null;
	private FileLock lock_ = null;
	private String dir;
	private String fileName;
	private int lockAcquisitionMaxAttempts;
	private long lockAcquisitionRetryDelay;

	public LogFileLock(String dir, String fileName) {
		if(!dir.endsWith(FILE_SEPARATOR)) {
			dir += FILE_SEPARATOR;
		}
		this.dir = dir;
		this.fileName = fileName;
		this.lockAcquisitionMaxAttempts = Configuration.getConfigProperties().getLockAcquisitionMaxAttempts();
		this.lockAcquisitionRetryDelay = Configuration.getConfigProperties().getLockAcquisitionRetryDelay();
	}

	public void acquireLock() throws LogException {
		try {
			File parent = new File(dir);
			if(!parent.exists()) {
				parent.mkdirs();
			}
			lockfileToPreventDoubleStartup_ = new File(dir, fileName + ".lck");
			lockfilestream_ = new FileOutputStream(lockfileToPreventDoubleStartup_);
			FileLock tryLock = tryAcquiringLock(1);
			if (tryLock != null) {
				lock_ = tryLock;
				lockfileToPreventDoubleStartup_.deleteOnExit();
				return;
			}
		} catch (IOException failedToGetLock) {
			// fail like below...
		}
		String msg = "The specified log seems to be in use already: " + fileName + " in "+ dir+". Make sure that no other instance is running, or kill any pending process if needed.";
		LOGGER.logFatal(msg);
		throw new LogException(msg);
	}

	private FileLock tryAcquiringLock(int currentTryCount) throws IOException {
		try {
			FileLock fileLock = lockfilestream_.getChannel().tryLock();
			if(fileLock != null) {
				return fileLock;
			}
		} catch (OverlappingFileLockException | IOException failedToGetLock) {
			LOGGER.logTrace("Trying to get lock failed with exception...", failedToGetLock);
		}
		// if we are here: either fileLock was null, OR we got an exception => outcome is the same: retry or give up
		if (currentTryCount < lockAcquisitionMaxAttempts) {
			LOGGER.logWarning("Couldn't acquire lock, will try again in " + lockAcquisitionRetryDelay + " millis...");
			try {
				TimeUnit.MILLISECONDS.sleep(lockAcquisitionRetryDelay);
			} catch (InterruptedException ie) {
				InterruptedExceptionHelper.handleInterruptedException(ie);
			}
			return tryAcquiringLock(currentTryCount + 1);
		}
		// out of retries and still no prior return with a lock => give up and fail
		throw new IOException("The file lock couldn't be acquired and no more retries allowed.");
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
