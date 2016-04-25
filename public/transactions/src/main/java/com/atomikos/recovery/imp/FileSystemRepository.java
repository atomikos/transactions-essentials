/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.imp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectStreamException;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.imp.LogFileLock;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.Repository;
import com.atomikos.util.VersionedFile;

public class FileSystemRepository implements
		Repository {

	private static final Logger LOGGER = LoggerFactory
			.createLogger(FileSystemRepository.class);
	private VersionedFile file;
	private FileChannel rwChannel = null;
	private LogFileLock lock_;
	@Override
	public void init() throws LogException {
		ConfigProperties configProperties =	Configuration.getConfigProperties();
		String baseDir = configProperties.getLogBaseDir();
		String baseName = configProperties.getLogBaseName();
		LOGGER.logDebug("baseDir "+baseDir);
		LOGGER.logDebug("baseName "+baseName);
        lock_ = new LogFileLock(baseDir, baseName);
        LOGGER.logDebug("LogFileLock "+lock_);
        lock_.acquireLock();
		file = new VersionedFile(baseDir, baseName, ".log");
		
	}

	private Serializer serializer = new Serializer();

	@Override
	public void put(String id, CoordinatorLogEntry coordinatorLogEntry)
			throws LogWriteException {
		
		try {
			initChannelIfNecessary();
			write(coordinatorLogEntry, true);
		} catch (IOException e) {
			throw new LogWriteException(e);
		}
	}

	private synchronized void initChannelIfNecessary() throws FileNotFoundException {
		if (rwChannel == null) {
			rwChannel = file.openNewVersionForNioWriting();
		}
	}

	private void write(CoordinatorLogEntry coordinatorLogEntry,
			boolean flushImmediately) throws IOException {
		String str = serializer.toJSON(coordinatorLogEntry);
		byte[] buffer = str.getBytes();
		ByteBuffer buff = ByteBuffer.wrap(buffer);
		writeToFile(buff, coordinatorLogEntry.shouldSync() && flushImmediately);
	}

	private synchronized void writeToFile(ByteBuffer buff, boolean force)
			throws IOException {
		rwChannel.write(buff);
		if (force) {
			rwChannel.force(false);
		}

	}


	@Override
	public CoordinatorLogEntry get(String coordinatorId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<CoordinatorLogEntry> findAllCommittingCoordinatorLogEntries() {
		throw new UnsupportedOperationException();
	}



	@Override
	public Collection<CoordinatorLogEntry> getAllCoordinatorLogEntries()
			throws LogReadException {
		Map<String, CoordinatorLogEntry> coordinatorLogEntries = new HashMap<String, CoordinatorLogEntry>();
		BufferedReader br = null;
		try {
			FileInputStream fis = file.openLastValidVersionForReading();
			InputStreamReader isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			coordinatorLogEntries = readContent( br);
		} catch (FileNotFoundException firstStart) {
			// the file could not be opened for reading;
			// merely return the default empty vector
		} catch (Exception e) {
			LOGGER.logFatal("Error in recover", e);
			throw new LogReadException(e);
		} finally {
			closeSilently(br);
		}

		return coordinatorLogEntries.values();
	}

	Map<String, CoordinatorLogEntry> readContent(
			BufferedReader br) throws IOException {
		
		Map<String, CoordinatorLogEntry> coordinatorLogEntries = new HashMap<String, CoordinatorLogEntry>();
		try {
			String line;
			while ((line = br.readLine()) != null) {
				CoordinatorLogEntry coordinatorLogEntry = deserialize(line);
				coordinatorLogEntries.put(coordinatorLogEntry.id,
					coordinatorLogEntry);
			}

		} catch (java.io.EOFException unexpectedEOF) {
			LOGGER.logTrace("Unexpected EOF - logfile not closed properly last time?", unexpectedEOF);
			// merely return what was read so far...
		} catch (StreamCorruptedException unexpectedEOF) {
			LOGGER.logTrace("Unexpected EOF - logfile not closed properly last time?", unexpectedEOF);
			// merely return what was read so far...
		} catch (ObjectStreamException unexpectedEOF) {
			LOGGER.logTrace("Unexpected EOF - logfile not closed properly last time?", unexpectedEOF);
			// merely return what was read so far...
		} catch (DeserialisationException unexpectedEOF) {
			LOGGER.logTrace("Unexpected EOF - logfile not closed properly last time? "+ unexpectedEOF);
		}
		return coordinatorLogEntries;
	}

	

	private void closeSilently(BufferedReader fis) {
		try {
			if (fis != null)
				fis.close();
		} catch (IOException io) {
			LOGGER.logWarning("Fail to close logfile after reading - ignoring");
		}
	}

	private Deserializer deserializer = new Deserializer();

	private CoordinatorLogEntry deserialize(String line) throws DeserialisationException {
		return deserializer.fromJSON(line);
	}

	@Override
	public void close() {
		try {
			closeOutput();
		} catch (Exception e) {
			LOGGER.logWarning("Error closing file - ignoring",e);
		} finally {
			lock_.releaseLock();	
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
	public synchronized void writeCheckpoint(
			Collection<CoordinatorLogEntry> checkpointContent)
			throws LogWriteException {

		try {
			closeOutput();

			rwChannel = file.openNewVersionForNioWriting();
			for (CoordinatorLogEntry coordinatorLogEntry : checkpointContent) {
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

}
