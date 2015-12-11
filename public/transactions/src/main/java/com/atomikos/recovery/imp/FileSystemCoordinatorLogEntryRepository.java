package com.atomikos.recovery.imp;

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
import java.util.HashMap;
import java.util.Map;

import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.CoordinatorLogEntryRepository;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.util.VersionedFile;

public class FileSystemCoordinatorLogEntryRepository implements
		CoordinatorLogEntryRepository {

	private static final Logger LOGGER = LoggerFactory
			.createLogger(FileSystemCoordinatorLogEntryRepository.class);
	private VersionedFile file;
	private FileChannel rwChannel = null;

	@Override
	public void init(ConfigProperties configProperties) {
		String baseDir = configProperties.getLogBaseDir();
		String baseName = configProperties.getLogBaseName();
		file = new VersionedFile(baseDir, baseName, ".log");
	}

	private Serializer serializer = new Serializer();

	@Override
	public void put(String id, CoordinatorLogEntry coordinatorLogEntry)
			throws LogWriteException {
		initChannelIfNecessary();
		try {
			write(coordinatorLogEntry, true);
		} catch (IOException e) {
			throw new LogWriteException(e);
		}
	}

	private synchronized void initChannelIfNecessary() {
		if (rwChannel == null) {
			rwChannel = initializeOutput();
		}
	}

	private void write(CoordinatorLogEntry coordinatorLogEntry,
			boolean flushImmediately) throws IOException {
		String str = serializer.toJSON(coordinatorLogEntry);
		byte[] buffer = str.getBytes();
		ByteBuffer buff = ByteBuffer.wrap(buffer);
		buff.put(buffer);
		buff.rewind();
		writeToFile(buff, coordinatorLogEntry.shouldSync() && flushImmediately);
	}

	private synchronized void writeToFile(ByteBuffer buff, boolean force)
			throws IOException {
		rwChannel.write(buff);
		if (force) {
			rwChannel.force(false);
		}

	}

	private FileChannel initializeOutput() {
		FileChannel rwChannel = null;
		try {
			rwChannel = file.openNewVersionForNioWriting();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rwChannel;
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
	public void close() {
		try {
			if (file != null) {
				file.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Collection<CoordinatorLogEntry> getAllCoordinatorLogEntries()
			throws LogReadException {
		Map<String, CoordinatorLogEntry> coordinatorLogEntries = new HashMap<String, CoordinatorLogEntry>();
		// TODO Open File for reading
		// perform a checkpoint
		// read file
		FileInputStream fis = null;
		try {
			String line;
			fis = file.openLastValidVersionForReading();
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);

			while ((line = br.readLine()) != null) {
				CoordinatorLogEntry coordinatorLogEntry = deserialize(line);
				coordinatorLogEntries.put(coordinatorLogEntry.coordinatorId,
						coordinatorLogEntry);
			}
			br.close();

		} catch (java.io.EOFException unexpectedEOF) {
			LOGGER.logDebug("Unexpected EOF - logfile not closed properly last time?", unexpectedEOF);
			// merely return what was read so far...
		} catch (StreamCorruptedException unexpectedEOF) {
			LOGGER.logDebug("Unexpected EOF - logfile not closed properly last time?", unexpectedEOF);
			// merely return what was read so far...
		} catch (ObjectStreamException unexpectedEOF) {
			LOGGER.logDebug("Unexpected EOF - logfile not closed properly last time?", unexpectedEOF);
			// merely return what was read so far...
		} catch (FileNotFoundException firstStart) {
			// the file could not be opened for reading;
			// merely return the default empty vector
		} catch (Exception e) {
			LOGGER.logWarning("Error in recover", e);
			throw new LogReadException(e);
		} finally {
			closeSilently(fis);
		}

		return coordinatorLogEntries.values();
	}

	private void closeSilently(InputStream fis) {
		try {
			if (fis != null)
				fis.close();
		} catch (IOException io) {
			LOGGER.logWarning("Fail to close logfile after reading - ignoring");
		}
	}

	private Deserializer deserializer = new Deserializer();

	private CoordinatorLogEntry deserialize(String line) {
		return deserializer.fromJSON(line);
	}

	protected void closeOutput() throws IllegalStateException {

		// try to close the previous output stream, if any.
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
		} catch (Exception e) {
			throw new LogWriteException(e);
		}

	}

}
