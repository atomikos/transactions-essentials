/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.fs;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectStreamException;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.imp.LogFileLock;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.PendingTransactionRecord;
import com.atomikos.util.VersionedFile;

public class FileSystemRepository implements Repository {

	private static final Logger LOGGER = LoggerFactory.createLogger(FileSystemRepository.class);
	private VersionedFile file;
	private FileChannel rwChannel = null;
	private LogFileLock lock_;

	@Override
	public void init() throws LogException {
		ConfigProperties configProperties = Configuration.getConfigProperties();
		String baseDir = configProperties.getLogBaseDir();
		String baseName = configProperties.getLogBaseName();
		LOGGER.logDebug("baseDir " + baseDir);
		LOGGER.logDebug("baseName " + baseName);
		lock_ = new LogFileLock(baseDir, baseName);
		LOGGER.logDebug("LogFileLock " + lock_);
		lock_.acquireLock();
		file = new VersionedFile(baseDir, baseName, ".log");

	}
	
	@Override
	public void put(String id, PendingTransactionRecord pendingTransactionRecord)
			throws IllegalArgumentException, LogWriteException {

		try {
			initChannelIfNecessary();
			write(pendingTransactionRecord, true);
		} catch (IOException e) {
			throw new LogWriteException(e);
		}
	}

	private synchronized void initChannelIfNecessary()
			throws FileNotFoundException {
		if (rwChannel == null) {
			rwChannel = file.openNewVersionForNioWriting();
		}
	}
	private void write(PendingTransactionRecord pendingTransactionRecord,
			boolean flushImmediately) throws IOException {
		String str = pendingTransactionRecord.toRecord();
		byte[] buffer = str.getBytes();
		ByteBuffer buff = ByteBuffer.wrap(buffer);
		writeToFile(buff, flushImmediately);
	}

	private synchronized void writeToFile(ByteBuffer buff, boolean force)
			throws IOException {
		rwChannel.write(buff);
		if (force) {
			rwChannel.force(false);
		}
	}

	@Override
	public PendingTransactionRecord get(String coordinatorId) throws LogReadException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<PendingTransactionRecord> findAllCommittingCoordinatorLogEntries() throws LogReadException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<PendingTransactionRecord> getAllCoordinatorLogEntries() throws LogReadException {
		FileInputStream fis = null;
		try {
			fis = file.openLastValidVersionForReading();
		} catch (FileNotFoundException firstStart) {
			// the file could not be opened for reading;
			// merely return the default empty vector
		} 
		if (fis != null) {
			return readFromInputStream(fis);
		}
		//else
		return Collections.emptyList();
	}

	public static Collection<PendingTransactionRecord> readFromInputStream(
			InputStream in) throws LogReadException {
		Map<String, PendingTransactionRecord> coordinatorLogEntries = new HashMap<String, PendingTransactionRecord>();
		BufferedReader br = null;
		try {
			InputStreamReader isr = new InputStreamReader(in);
			br = new BufferedReader(isr);
			coordinatorLogEntries = readContent(br);
		} catch (Exception e) {
			LOGGER.logFatal("Error in recover", e);
			throw new LogReadException(e);
		} finally {
			closeSilently(br);
		}
		return coordinatorLogEntries.values();
	}
	
	static Map<String, PendingTransactionRecord> readContent(BufferedReader br)
			throws IOException {

		Map<String, PendingTransactionRecord> coordinatorLogEntries = new HashMap<String, PendingTransactionRecord>();
		String line = null;
		try {

			while ((line = br.readLine()) != null) {
				if (line.startsWith("{\"id\"")) {
					String msg = "Detected old log file format - please terminate all transactions under the old release first!";
					throw new IOException(msg);
				}
				PendingTransactionRecord coordinatorLogEntry = PendingTransactionRecord.fromRecord(line);
				coordinatorLogEntries.put(coordinatorLogEntry.id,coordinatorLogEntry);
			}

		} catch (java.io.EOFException unexpectedEOF) {
			LOGGER.logTrace(
					"Unexpected EOF - logfile not closed properly last time?",
					unexpectedEOF);
			// merely return what was read so far...
		} catch (StreamCorruptedException unexpectedEOF) {
			LOGGER.logTrace(
					"Unexpected EOF - logfile not closed properly last time?",
					unexpectedEOF);
			// merely return what was read so far...
		} catch (ObjectStreamException unexpectedEOF) {
			LOGGER.logTrace(
					"Unexpected EOF - logfile not closed properly last time?",
					unexpectedEOF);
			// merely return what was read so far...
		} catch (IllegalArgumentException couldNotParseLastRecord) {
			LOGGER.logTrace(
					"Unexpected record format - logfile not closed properly last time?",
					couldNotParseLastRecord);
			// merely return what was read so far...
		} catch (RuntimeException unexpectedEOF) {
			LOGGER.logWarning("Unexpected EOF - logfile not closed properly last time? " +line +" "
					+ unexpectedEOF);
		}
		return coordinatorLogEntries;
	}
	private static void closeSilently(BufferedReader fis) {
		try {
			if (fis != null)
				fis.close();
		} catch (IOException io) {
			LOGGER.logWarning("Fail to close logfile after reading - ignoring");
		}
	}
	
	@Override
	public void writeCheckpoint(Collection<PendingTransactionRecord> checkpointContent) throws LogWriteException {

		try {
			closeOutput();

			rwChannel = file.openNewVersionForNioWriting();
			for (PendingTransactionRecord coordinatorLogEntry : checkpointContent) {
				write(coordinatorLogEntry, false);
			}
			rwChannel.force(false);
			file.discardBackupVersion();
		} catch (FileNotFoundException firstStart) {
			// the file could not be opened for reading;
			// merely return the default empty vector
		} catch (Exception e) {
			LOGGER.logFatal("Failed to write checkpoint", e);
			throw new LogWriteException(e);
		}
	}
	
	protected void closeOutput() throws IllegalStateException {
		try {
			if (file != null) {
				file.close();
			}
		} catch (IOException e) {
			throw new IllegalStateException("Error closing previous output", e);
		}
	}
	
	@Override
	public void close() {
		try {
			closeOutput();
		} catch (Exception e) {
			LOGGER.logWarning("Error closing file - ignoring", e);
		} finally {
			lock_.releaseLock();
		}

	}

}
