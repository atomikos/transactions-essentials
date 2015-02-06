package com.atomikos.persistence.imp;

import java.io.FileOutputStream;
import java.io.IOException;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.LogException;
import com.atomikos.util.VersionedFile;

public class AbstractLogStream {

	protected static final Logger LOGGER = LoggerFactory.createLogger(AbstractLogStream.class);
	protected FileOutputStream output_;
	protected boolean simulateCrash_;
	protected boolean corrupt_;
	protected VersionedFile file_;

	public AbstractLogStream(String baseDir, String baseName) {
		file_ = new VersionedFile(baseDir, baseName, ".log");
		simulateCrash_ = false;

		corrupt_ = false;

	}

	protected void closeOutput() throws LogException {
	
		// try to close the previous output stream, if any.
		try {
			if (file_ != null) {
				file_.close();
				if (LOGGER.isInfoEnabled()) {
					LOGGER.logInfo("Logfile closed: " + file_.getCurrentVersionFileName());
				}
			}
			output_ = null;
			// ooutput_ = null;
		} catch (IOException e) {
	
			throw new LogException("Error closing previous output", e);
		}
	}

	/**
	 * Makes write checkpoint crash before old file delete.
	 * 
	 * For debugging only.
	 */
	public void setCrashMode() {
		simulateCrash_ = true;
	}

	public synchronized void close() throws LogException {
		closeOutput();
	}

	public void finalize() throws Throwable {
		try {
			close();
		} finally {
			super.finalize();
		}
	}

	public long getSize() throws LogException {
		return file_.getSize();
	}

}