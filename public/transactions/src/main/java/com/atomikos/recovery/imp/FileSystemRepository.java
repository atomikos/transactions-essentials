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

import com.atomikos.icatch.CoordinatorLogEntry;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.Repository;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.util.VersionedFile;

public class FileSystemRepository implements
		Repository {

	private static final Logger LOGGER = LoggerFactory
			.createLogger(FileSystemRepository.class);
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
		FileInputStream fis = null;
		try {
			String line;
			fis = file.openLastValidVersionForReading();
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);

			while ((line = br.readLine()) != null) {
				CoordinatorLogEntry coordinatorLogEntry = deserialize(line);
				coordinatorLogEntries.put(coordinatorLogEntry.id,
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

	@Override
	public void close() {
		try {
			closeOutput();
		} catch (Exception e) {
			LOGGER.logWarning("Error closing file - ignoring",e);
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
			throw new LogWriteException(e);
		}

	}

}
