package com.atomikos.recovery.imp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.CoordinatorLogEntryRepository;
import com.atomikos.recovery.LogWriteException;
import com.atomikos.recovery.ParticipantLogEntry;
import com.atomikos.util.VersionedFile;

public class FileSystemCoordinatorLogEntryRepository implements
		CoordinatorLogEntryRepository {

	private VersionedFile file;
	private long checkpointInterval;
	private FileOutputStream fos;
	@Override
	public void init(ConfigProperties configProperties) {
		String baseDir = configProperties.getLogBaseDir();
		String baseName = configProperties.getLogBaseName();
		file = new VersionedFile(baseDir, baseName, ".log");
		checkpointInterval = configProperties.getCheckpointInterval();
		//read file
		//write 
		try {
			fos = file.openNewVersionForWriting();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void put(String id, CoordinatorLogEntry coordinatorLogEntry)
			throws IllegalArgumentException, LogWriteException {
		try {
			fos.write(10);
			fos.getFD().sync();
		} catch (IOException e) {
			throw new LogWriteException();
		}

	}

	@Override
	public void remove(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public CoordinatorLogEntry get(String coordinatorId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ParticipantLogEntry> findAllCommittingParticipants() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<CoordinatorLogEntry> getAllCoordinatorLogEntries() {
		// TODO Open File for reading
		//perform a checkpoint
		
		return null;
	}

}
