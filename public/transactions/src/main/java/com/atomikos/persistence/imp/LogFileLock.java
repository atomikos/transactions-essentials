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
import java.time.Duration;
import java.time.Instant;
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
			tryAcquiringLock();
			lockfileToPreventDoubleStartup_.deleteOnExit();
			return;
		} catch (OverlappingFileLockException | IOException failedToGetLock) {
			// either may happen on windows
		}
		String msg = "The specified log seems to be in use already: " + fileName + " in "+ dir+". Make sure that no other instance is running, or kill any pending process if needed.";
		LOGGER.logFatal(msg);
		throw new LogException(msg);
	}

	/**
	 * Acquiring lock for transactions.
	 *
	 * @throws OverlappingFileLockException either may happen on windows
	 * @throws IOException                  if the lock couldn't be acquired
	 */
	private void tryAcquiringLock() throws OverlappingFileLockException, IOException {
		int currentRetryCount = 0;
		do {
			try {
				File parent = new File(dir);
				if (!parent.exists()) {
					parent.mkdirs();
				}
				if (lockfileToPreventDoubleStartup_ == null || !lockfileToPreventDoubleStartup_.exists()) {
					lockfileToPreventDoubleStartup_ = new File(dir, fileName + ".lck");
					lockfilestream_ = new FileOutputStream(lockfileToPreventDoubleStartup_);
				}
				lock_ = lockfilestream_.getChannel().tryLock();
				if (lock_ != null) {
					return;
				}
				LOGGER.logWarning(String.format("File lock couldn't be acquired file lock (dir: %s, file: %s) is null", dir, fileName));
			} catch (IOException ioException) {
				// In this case we continue to check in the loop
				LOGGER.logWarning("File lock could not be acquired, another process holds the lock, waiting.", ioException);
			} catch (OverlappingFileLockException e) {
				LOGGER.logWarning(String.format("Lock could not be obtained because of overlapping file lock exception (dir: %s, file: %s)", dir, fileName));
				throw e;
			}
			sleep();
			currentRetryCount += 1;
		} while (currentRetryCount < lockAcquisitionMaxAttempts);

		throw new RuntimeException("The lock couldn't be acquired on ");
	}

	/**
	 * Sleep while lock file checking.
	 */
	private void sleep() {
		try {
			TimeUnit.MILLISECONDS.sleep(lockAcquisitionRetryDelay);
		} catch (InterruptedException interruptedException) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("The thread has been interrupted waiting for lock acquisition.", interruptedException);
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
